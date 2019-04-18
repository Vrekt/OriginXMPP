package me.vrekt.origin;

import io.github.robertograham.nadir.Account;
import io.github.robertograham.nadir.Nadir;
import me.vrekt.origin.chat.implementation.ChatResource;
import me.vrekt.origin.chat.implementation.ChatResourceImpl;
import me.vrekt.origin.exception.XMPPAuthenticationException;
import me.vrekt.origin.extension.ActivityExtension;
import me.vrekt.origin.friend.implementation.FriendResource;
import me.vrekt.origin.friend.implementation.FriendResourceImpl;
import me.vrekt.origin.listener.ConnectListener;
import me.vrekt.origin.lobby.implementation.LobbyResource;
import me.vrekt.origin.lobby.implementation.LobbyResourceImpl;
import me.vrekt.origin.presence.GameTextPresence;
import me.vrekt.origin.presence.implementation.PresenceResource;
import me.vrekt.origin.presence.implementation.PresenceResourceImpl;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import org.jivesoftware.smackx.caps.packet.CapsExtension;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.Jid;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class DefaultOrigin implements Origin {
    private final Map<Boolean, CopyOnWriteArrayList<ConnectListener>> listeners = new ConcurrentHashMap<>();
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> reconnectFuture;

    private GameTextPresence initialPresence;

    private Nadir nadir;
    private Account account;
    private Jid user;

    private ChatResourceImpl chatResource;
    private FriendResourceImpl friendResource;
    private PresenceResourceImpl presenceResource;
    private LobbyResourceImpl lobbyResource;

    private XMPPTCPConnection connection;
    private PingManager pings;

    private boolean reconnect = true, scheduled;
    private final String emailAddress, password;

    /**
     * Initializes this instance
     *
     * @param emailAddress the email address of the account
     * @param password     the password of the account
     */
    DefaultOrigin(final String emailAddress, final String password) {
        this.emailAddress = emailAddress;
        this.password = password;
        try {
            nadir = Nadir.newNadir(emailAddress, password);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Initialize this instance
     *
     * @param emailAddress the email address of the account
     * @param password     the password of the account
     * @param presence     the initial presence to send on connect.
     */
    DefaultOrigin(final String emailAddress, final String password, final GameTextPresence presence) {
        this(emailAddress, password);
        this.initialPresence = presence;
    }

    /**
     * Initializes this instance
     *
     * @param nadir the already built instance of {@link Nadir}
     */
    DefaultOrigin(final Nadir nadir) {
        this.nadir = nadir;
        this.emailAddress = nadir.emailAddress();
        this.password = nadir.password();
    }

    /**
     * Initializes this instance
     *
     * @param nadir    the already built instance of {@link Nadir}
     * @param presence the initial presence to send on connect
     */
    DefaultOrigin(final Nadir nadir, final GameTextPresence presence) {
        this(nadir);
        this.initialPresence = presence;
    }

    @Override
    public void connect() throws XMPPAuthenticationException {
        try {
            nadir.accounts().findOneBySessionToken().ifPresent(session -> this.account = session);
            connectTo(CHAT_DOMAIN);
        } catch (final IOException exception) {
            throw new XMPPAuthenticationException("Failed to connect!", exception);
        }
    }

    private void connectTo(final String host) throws XMPPAuthenticationException {
        try {
            final var userId = Long.toString(account.userId());

            connection = new XMPPTCPConnection(
                    XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(userId, nadir.password())
                            .setXmppDomain(CHAT_DOMAIN)
                            .setHost(host)
                            .setPort(SERVICE_PORT)
                            .setResource("origin")
                            .setSendPresence(false)
                            .build());

            connection.setFromMode(XMPPConnection.FromMode.USER);
            // accept all roster entries.
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);

            // enable caps manager for activity.
            final var mgr = EntityCapsManager.getInstanceFor(connection);
            mgr.enableEntityCaps();
            mgr.setEntityNode("http://www.origin.com/origin");
            ProviderManager.addExtensionProvider(ActivityExtension.ELEMENT, ActivityExtension.NAMESPACE, ActivityExtension.provider());

            Presence sendPresence;
            if (initialPresence != null) {
                sendPresence = new Presence(Presence.Type.available, initialPresence.status(), 0, Presence.Mode.available);
                sendPresence.addExtension(new ActivityExtension(initialPresence.activity()));
                sendPresence.addExtension(new CapsExtension("http://www.origin.com/origin", mgr.getCapsVersionAndHash().version, mgr.getCapsVersionAndHash().hash));
            } else {
                sendPresence = new Presence(Presence.Type.available);
            }

            connection.connect().login();
            connection.sendStanza(sendPresence);

            this.user = connection.getUser();
            this.pings = PingManager.getInstanceFor(connection);
            pings.setPingInterval(60);

            if (reconnect) {
                // minus 5 minutes to be safe
                final var expiresWhen = Duration.between(LocalDateTime.now(), nadir.session().accessTokenExpiresAt().minusMinutes(5)).toMillis();
                reconnectFuture = service.schedule(this::reconnect, expiresWhen, TimeUnit.MILLISECONDS);
                scheduled = true;
            }
            initializeAll();

            if (listeners.containsKey(Boolean.TRUE)) {
                listeners.get(Boolean.TRUE).forEach(ConnectListener::onConnectOrReconnect);
            }
        } catch (final IOException | SmackException | XMPPException | InterruptedException exception) {
            if (exception instanceof XMPPException.StreamErrorException) {
                // get the other host required for connecting.
                final var streamError = (XMPPException.StreamErrorException) exception;
                final var error = streamError.getStreamError();
                if (StreamError.Condition.see_other_host.equals(error.getCondition())) {
                    connectTo(error.getConditionText());
                }
            } else {
                // other host not found, throw
                throw new XMPPAuthenticationException("Failed to connect!", exception);
            }
        }
    }

    /**
     * Reconnects to the XMPP service.
     */
    private void reconnect() {
        if (listeners.containsKey(Boolean.FALSE)) {
            listeners.get(Boolean.FALSE).forEach(ConnectListener::onConnectOrReconnect);
        }
        clearAll();

        nadir.close();
        pings.setPingInterval(-1);
        connection.disconnect();
        try {
            nadir = Nadir.newNadir(emailAddress, password);
            connectTo(CHAT_DOMAIN);
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Clear all resources
     */
    private void clearAll() {
        chatResource.clear();
        friendResource.clear();
        presenceResource.clear();
        lobbyResource.clear();
    }

    /**
     * Initialize or re-initialize resources.
     */
    private void initializeAll() {
        chatResource = chatResource == null ? new ChatResourceImpl(connection) : chatResource.reinitialize(connection);
        friendResource = friendResource == null ? new FriendResourceImpl(connection) : friendResource.reinitialize(connection);
        presenceResource = presenceResource == null ? new PresenceResourceImpl(connection) : presenceResource.reinitialize(connection);
        lobbyResource = lobbyResource == null ? new LobbyResourceImpl(connection) : lobbyResource.reinitialize(connection);
    }

    @Override
    public void disconnect() {
        chatResource.dispose();
        friendResource.dispose();
        presenceResource.dispose();
        lobbyResource.dispose();
        nadir.close();

        pings.setPingInterval(-1);
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
    public ChatResource chat() {
        return chatResource;
    }

    @Override
    public FriendResource friend() {
        return friendResource;
    }

    @Override
    public PresenceResource presence() {
        return presenceResource;
    }

    @Override
    public LobbyResource lobby() {
        return lobbyResource;
    }

    @Override
    public XMPPTCPConnection connection() {
        return connection;
    }

    @Override
    public void onConnect(final ConnectListener listener) {
        listeners.putIfAbsent(Boolean.TRUE, new CopyOnWriteArrayList<>());
        listeners.get(Boolean.TRUE).add(listener);
    }

    @Override
    public void onReconnect(final ConnectListener listener) {
        listeners.putIfAbsent(Boolean.FALSE, new CopyOnWriteArrayList<>());
        listeners.get(Boolean.FALSE).add(listener);
    }

    @Override
    public void disableAutomaticReconnect() {
        reconnect = false;
        scheduled = false;
        if (reconnectFuture != null) reconnectFuture.cancel(false);
    }

    @Override
    public void enableAutomaticReconnect() {
        reconnect = true;
        if (!scheduled) {
            final var expiresWhen = Duration.between(LocalDateTime.now(), nadir.session().accessTokenExpiresAt().minusMinutes(5)).toMillis();
            reconnectFuture = service.schedule(this::reconnect, expiresWhen, TimeUnit.MILLISECONDS);
            scheduled = true;
        }
    }
}
