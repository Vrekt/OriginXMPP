package me.vrekt.origin;

import io.github.robertograham.nadir.Account;
import io.github.robertograham.nadir.Nadir;
import me.vrekt.origin.chat.ChatService;
import me.vrekt.origin.chat.implementation.DefaultChatService;
import me.vrekt.origin.exception.OriginException;
import me.vrekt.origin.friend.DefaultFriendService;
import me.vrekt.origin.friend.FriendService;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;

import java.io.IOException;

public final class DefaultOrigin implements Origin {

    public static final String CHAT_DOMAIN = "chat.dm.origin.com";
    public static final String HOST_DOMAIN = "0394d6e52c011e300.gs.ea.com";
    public static final int SERVICE_PORT = 5222;

    private Nadir nadir;
    private Account account;
    private BareJid user;

    private DefaultChatService chatService;
    private DefaultFriendService friendService;

    private XMPPTCPConnection connection;

    /**
     * Initializes this instance
     *
     * @param emailAddress the email address of the account
     * @param password     the password of the account
     */
    DefaultOrigin(final String emailAddress, final String password) {
        try {
            nadir = Nadir.newNadir(emailAddress, password);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Initializes this instance
     *
     * @param nadir the already built instance of {@link Nadir}
     */
    DefaultOrigin(final Nadir nadir) {
        this.nadir = nadir;
    }

    @Override
    public void connect() throws OriginException {
        try {
            nadir.accounts().findOneBySessionToken().ifPresent(acc -> this.account = acc);

            final var token = nadir.session().accessToken();
            final var userId = Long.toString(account.userId());

            connection = new XMPPTCPConnection(
                    XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(userId, token)
                            .setXmppDomain(CHAT_DOMAIN)
                            .setHost(HOST_DOMAIN)
                            .setPort(SERVICE_PORT)
                            .build());

            connection.connect().login(userId, nadir.password(), Resourcepart.fromOrThrowUnchecked("origin"));
            this.user = JidCreate.bareFrom(userId + "@" + CHAT_DOMAIN);

            connection.setFromMode(XMPPConnection.FromMode.USER);
            final var pingManager = PingManager.getInstanceFor(connection);
            pingManager.setPingInterval(60);

            chatService = new DefaultChatService(connection);
            friendService = new DefaultFriendService(connection);

            System.err.println("Connected to the origin XMPP service.");
        } catch (final IOException | SmackException | XMPPException | InterruptedException exception) {
            throw new OriginException("Could not connect to XMPP service!", exception.getCause());
        }
    }

    @Override
    public void disconnect() {
        System.err.println("Disconnecting from the origin XMPP service.");
        chatService.close();
        friendService.close();
        nadir.close();

        PingManager.getInstanceFor(connection).setPingInterval(-1);
        connection.disconnect();
    }

    @Override
    public Nadir getNadir() {
        return nadir;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public BareJid getUser() {
        return user;
    }

    @Override
    public ChatService chat() {
        return chatService;
    }

    @Override
    public FriendService friend() {
        return friendService;
    }

    @Override
    public XMPPTCPConnection getConnection() {
        return connection;
    }
}
