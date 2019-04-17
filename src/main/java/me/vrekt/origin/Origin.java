package me.vrekt.origin;

import io.github.robertograham.nadir.Account;
import io.github.robertograham.nadir.Nadir;
import me.vrekt.origin.chat.implementation.ChatResource;
import me.vrekt.origin.exception.XMPPAuthenticationException;
import me.vrekt.origin.friend.implementation.FriendResource;
import me.vrekt.origin.listener.ConnectListener;
import me.vrekt.origin.lobby.implementation.LobbyResource;
import me.vrekt.origin.presence.GameTextPresence;
import me.vrekt.origin.presence.implementation.PresenceResource;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.Jid;

public interface Origin {

    String CHAT_DOMAIN = "chat.dm.origin.com";
    int SERVICE_PORT = 5222;

    /**
     * Attempts to connect the XMPP service.
     *
     * @throws XMPPAuthenticationException if an error occurred attempting to connect to the XMPP service.
     */
    void connect() throws XMPPAuthenticationException;

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
     * @return the internal instance of {@link ChatResource}.
     */
    ChatResource chat();

    /**
     * @return the internal instance of {@link FriendResource}
     */
    FriendResource friend();

    /**
     * @return the internal instance of {@link PresenceResource}
     */
    PresenceResource presence();

    /**
     * @return the internal instance of {@link LobbyResource}
     */
    LobbyResource lobby();

    /**
     * @return the internal connection instance.
     */
    XMPPTCPConnection connection();

    /**
     * Adds a new {@link ConnectListener}
     *
     * @param listener the listener
     */
    void onConnect(final ConnectListener listener);

    /**
     * Adds a new {@link ConnectListener}
     *
     * @param listener the listener
     */
    void onReconnect(final ConnectListener listener);

    /**
     * Disables automatic reconnect after the {@link io.github.robertograham.nadir.Token} expires.
     */
    void disableAutomaticReconnect();

    /**
     * Enables automatic reconnect after the {@link io.github.robertograham.nadir.Token} expires.
     * Enabled by default.
     */
    void enableAutomaticReconnect();

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
     * @param emailAddress the email address of the account.
     * @param password     the password of the account.
     * @param presence     the initial presence to send on connect
     * @return a new instance of {@link Origin}
     */
    static Origin newOrigin(String emailAddress, String password, final GameTextPresence presence) {
        return new DefaultOrigin(emailAddress, password, presence);
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

    /**
     * Builds a new instance of {@link Origin}
     *
     * @param nadir    the already built instance of {@link Nadir}
     * @param presence the initial presence to send on connect
     * @return a new instance of {@link Origin}
     */
    static Origin newOrigin(final Nadir nadir, final GameTextPresence presence) {
        return new DefaultOrigin(nadir, presence);
    }

}
