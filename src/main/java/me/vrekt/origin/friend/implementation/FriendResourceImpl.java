package me.vrekt.origin.friend.implementation;

import com.google.common.flogger.FluentLogger;
import me.vrekt.origin.Origin;
import me.vrekt.origin.friend.listener.FriendListener;
import me.vrekt.origin.resource.ResourceInitializer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.impl.JidCreate;

import java.util.concurrent.CopyOnWriteArrayList;

public final class FriendResourceImpl implements FriendResource, ResourceInitializer<FriendResourceImpl> {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    private final CopyOnWriteArrayList<FriendListener> listeners = new CopyOnWriteArrayList<>();
    private final PacketListener packetListener = new PacketListener();
    private XMPPTCPConnection connection;

    public FriendResourceImpl(final XMPPTCPConnection connection) {
        this.connection = connection;
        connection.addAsyncStanzaListener(packetListener, StanzaTypeFilter.PRESENCE);
    }

    @Override
    public void addFriendListener(final FriendListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeFriendListener(final FriendListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean sendFriendRequest(final Long userId) {
        try {
            final var to = JidCreate.bareFromOrThrowUnchecked(userId + "@" + Origin.CHAT_DOMAIN);
            final var request = new Presence(to, Presence.Type.subscribe);
            connection.sendStanza(request);
            return true;
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            LOGGER.atSevere().withCause(exception).log("Could not send friend request to " + userId);
        }
        return false;
    }

    @Override
    public boolean removeFriend(final Long userId) {
        try {
            final var to = JidCreate.bareFromOrThrowUnchecked(userId + "@" + Origin.CHAT_DOMAIN);
            final var unavailable = new Presence(to, Presence.Type.unavailable);
            final var unsubscribe = new Presence(to, Presence.Type.unsubscribe);

            connection.sendStanza(unavailable);
            connection.sendStanza(unsubscribe);
            return true;
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            LOGGER.atSevere().withCause(exception).log("Could not remove friend " + userId);
        }
        return false;
    }

    @Override
    public boolean acceptFriendRequest(final Long userId) {
        try {
            final var to = JidCreate.bareFromOrThrowUnchecked(userId + "@" + Origin.CHAT_DOMAIN);
            final var subscribed = new Presence(to, Presence.Type.subscribed);
            final var available = new Presence(to, Presence.Type.available);

            connection.sendStanza(subscribed);
            connection.sendStanza(available);
            return true;
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            LOGGER.atSevere().withCause(exception).log("Could not accept friend request from " + userId);
        }
        return false;
    }

    @Override
    public FriendResourceImpl reinitialize(final XMPPTCPConnection connection) {
        this.connection = connection;
        connection.addAsyncStanzaListener(packetListener, StanzaTypeFilter.PRESENCE);
        return this;
    }

    @Override
    public void clear() {
        connection.removeAsyncStanzaListener(packetListener);
    }

    @Override
    public void dispose() {
        connection.removeAsyncStanzaListener(packetListener);
        listeners.clear();
    }

    /**
     * Listens for presence packets sent
     */
    private final class PacketListener implements StanzaListener {
        @Override
        public void processStanza(final Stanza packet) {

            final var presence = (Presence) packet;
            final var userId = Long.valueOf(presence.getFrom().getLocalpartOrNull().asUnescapedString());

            if (presence.getType() == Presence.Type.subscribe) {
                listeners.forEach(listener -> listener.onFriendRequestReceived(userId));
            } else if (presence.getType() == Presence.Type.subscribed) {
                listeners.forEach(listener -> listener.onFriendRequestAccepted(userId));
            }
        }
    }

}
