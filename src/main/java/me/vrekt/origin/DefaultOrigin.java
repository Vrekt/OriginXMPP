package me.vrekt.origin;

import io.github.robertograham.nadir.Account;
import io.github.robertograham.nadir.Nadir;
import me.vrekt.origin.chat.ChatService;
import me.vrekt.origin.chat.implementation.DefaultChatService;
import me.vrekt.origin.exception.OriginException;
import me.vrekt.origin.friend.DefaultFriendService;
import me.vrekt.origin.friend.FriendService;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

import java.io.IOException;

public final class DefaultOrigin implements Origin {

    private Nadir nadir;
    private Account account;
    private Jid user;

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

            final var userId = Long.toString(account.userId());
            connection = new XMPPTCPConnection(
                    XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(userId, nadir.password())
                            .setXmppDomain(CHAT_DOMAIN)
                            .setHost(HOST_DOMAIN)
                            .setPort(SERVICE_PORT)
                            .setResource(Resourcepart.fromOrThrowUnchecked("origin"))
                            .build());

            connection.connect().login();
            this.user = connection.getUser();

            final var pingManager = PingManager.getInstanceFor(connection);
            pingManager.setPingInterval(60);

            chatService = new DefaultChatService(connection);
            friendService = new DefaultFriendService(this);

            System.err.println("Connected to the origin XMPP service.");
        } catch (final IOException | SmackException | XMPPException | InterruptedException exception) {
            throw new OriginException("Cannot connect!", exception);
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
    public Nadir nadir() {
        return nadir;
    }

    @Override
    public Account account() {
        return account;
    }

    @Override
    public Jid user() {
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
    public XMPPTCPConnection connection() {
        return connection;
    }
}
