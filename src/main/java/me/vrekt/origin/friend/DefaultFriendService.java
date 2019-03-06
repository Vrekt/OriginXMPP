package me.vrekt.origin.friend;

import me.vrekt.origin.DefaultOrigin;
import me.vrekt.origin.exception.OriginException;
import me.vrekt.origin.friend.implementation.FriendListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.impl.JidCreate;

import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultFriendService implements FriendService {

    private final CopyOnWriteArrayList<FriendListener> listeners = new CopyOnWriteArrayList<>();
    private final PacketListener packetListener = new PacketListener();
    private final XMPPTCPConnection connection;

    public DefaultFriendService(final XMPPTCPConnection connection) {
        this.connection = connection;
        connection.addAsyncStanzaListener(packetListener, new StanzaTypeFilter(Presence.class));

        connection.addAsyncStanzaListener(packet -> {
            System.out.println(packet.toString());
        }, new StanzaTypeFilter(Stanza.class));

    }

    @Override
    public void addFriendListener(FriendListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeFriendListener(FriendListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void sendFriendRequest(Long userId) throws OriginException {
        try {
            final var to = JidCreate.bareFromOrThrowUnchecked(userId + "@" + DefaultOrigin.CHAT_DOMAIN);
            final var request = new Presence(to, Presence.Type.subscribe);
            connection.sendStanza(request);
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            throw new OriginException("Could not send request!", exception.getCause());
        }
    }

    @Override
    public void acceptFriendRequest(Long userId) throws OriginException {
        try {
            final var to = JidCreate.bareFromOrThrowUnchecked(userId + "@" + DefaultOrigin.CHAT_DOMAIN);
            final var subscribed = new Presence(to, Presence.Type.subscribed);
            final var available = new Presence(to, Presence.Type.available);
            connection.sendStanza(subscribed);
            connection.sendStanza(available);
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            throw new OriginException("Could not send request!", exception.getCause());
        }
    }

    @Override
    public void close() {
        connection.removeAsyncStanzaListener(packetListener);
        listeners.clear();
    }

    private final class PacketListener implements StanzaListener {
        @Override
        public void processStanza(Stanza packet) {

            final var presence = (Presence) packet;
            final var userId = Long.valueOf(presence.getFrom().getLocalpartOrNull().asUnescapedString());

            System.err.println("Type: " + presence.getType() + " From: " + presence.getFrom());

            if (presence.getType() == Presence.Type.subscribe) {
                listeners.forEach(listener -> listener.onFriendRequestReceived(userId));
            } else if (presence.getType() == Presence.Type.subscribed) {
                listeners.forEach(listener -> listener.onFriendRequestAccepted(userId));
            }

        }
    }

}
