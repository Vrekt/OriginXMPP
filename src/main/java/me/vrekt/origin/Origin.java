package me.vrekt.origin;

import io.github.robertograham.nadir.Account;
import io.github.robertograham.nadir.Nadir;
import me.vrekt.origin.chat.ChatService;
import me.vrekt.origin.exception.OriginException;
import me.vrekt.origin.friend.FriendService;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.Jid;

public interface Origin {

    String CHAT_DOMAIN = "chat.dm.origin.com";
    String HOST_DOMAIN = "0394d6e52c011e300.gs.ea.com";
    int SERVICE_PORT = 5222;

    /**
     * Attempts to connect the XMPP service.
     *
     * @throws OriginException if an error occurred while attempting to connect to the XMPP service.
     */
    void connect() throws OriginException;

    /**
     * Disconnects from the XMPP service, closes all services, and closes the {@link Nadir} instance.
     */
    void disconnect();

    /**
     * @return the internal instance of {@link Nadir}
     */
    Nadir nadir();

    /**
     * @return the current {@link Account} in use by the XMPP service.
     */
    Account account();

    /**
     * @return the current {@link Jid} of the connected user.
     * This will not include any resource.
     */
    Jid user();

    /**
     * @return the internal instance of {@link ChatService}.
     */
    ChatService chat();

    /**
     * @return the internal instance of {@link FriendService}
     */
    FriendService friend();

    /**
     * @return the internal connection instance.
     */
    XMPPTCPConnection connection();

    /**
     * Builds a new instance of {@link Origin}
     *
     * @param emailAddress the email address of the account.
     * @param password     the password of the account.
     * @return a new instance of {@link Origin}
     */
    static Origin newOrigin(String emailAddress, String password) {
        return new DefaultOrigin(emailAddress, password);
    }

    /**
     * Builds a new instance of {@link Origin}
     *
     * @param nadir the already built instance of {@link Nadir}
     * @return a new instance of {@link Origin}
     */
    static Origin newOrigin(Nadir nadir) {
        return new DefaultOrigin(nadir);
    }

}
