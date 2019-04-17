package me.vrekt.origin.presence.implementation;

import com.google.common.flogger.FluentLogger;
import me.vrekt.origin.extension.ActivityExtension;
import me.vrekt.origin.presence.GameTextPresence;
import me.vrekt.origin.resource.ResourceInitializer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import org.jivesoftware.smackx.caps.packet.CapsExtension;

public final class PresenceResourceImpl implements PresenceResource, ResourceInitializer<PresenceResourceImpl> {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    private XMPPTCPConnection connection;
    private EntityCapsManager capsManager;

    public PresenceResourceImpl(final XMPPTCPConnection connection) {
        this.connection = connection;
        this.capsManager = EntityCapsManager.getInstanceFor(connection);
    }

    @Override
    public void setGameTextPresence(final GameTextPresence presence) {
        final var sendPresence = new Presence(Presence.Type.available, presence.status(), 0, Presence.Mode.available);
        sendPresence.addExtension(new ActivityExtension(presence.activity()));
        sendPresence.addExtension(new CapsExtension("http://www.origin.com/origin", capsManager.getCapsVersionAndHash().version, capsManager.getCapsVersionAndHash().hash));
        send(sendPresence);
    }

    @Override
    public void setRawTextPresence(final String status, final String activity) {
        final var sendPresence = new Presence(Presence.Type.available, status, 0, Presence.Mode.available);
        sendPresence.addExtension(new ActivityExtension(activity));
        sendPresence.addExtension(new CapsExtension("http://www.origin.com/origin", capsManager.getCapsVersionAndHash().version, capsManager.getCapsVersionAndHash().hash));
        send(sendPresence);
    }

    @Override
    public void setPresence(final Presence presence) {
        send(presence);
    }

    @Override
    public PresenceResourceImpl reinitialize(final XMPPTCPConnection connection) {
        this.connection = connection;
        this.capsManager = EntityCapsManager.getInstanceFor(connection);
        return this;
    }

    @Override
    public void clear() {
        //
    }

    @Override
    public void dispose() {
        //
    }

    /**
     * Send the presence.
     *
     * @param presence the presence
     */
    private void send(final Presence presence) {
        try {
            connection.sendStanza(presence);
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            LOGGER.atSevere().withCause(exception).log("Could not send presence request!");
        }
    }

}
