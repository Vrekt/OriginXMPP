package me.vrekt.origin.lobby.implementation;

import com.google.common.flogger.FluentLogger;
import me.vrekt.origin.Origin;
import me.vrekt.origin.extension.ActivityExtension;
import me.vrekt.origin.lobby.Lobby;
import me.vrekt.origin.lobby.listener.LobbyListener;
import me.vrekt.origin.resource.ResourceInitializer;
import org.jivesoftware.smack.UnparseableStanza;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.parsing.ParsingExceptionCallback;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LobbyResourceImpl implements LobbyResource, ResourceInitializer<LobbyResourceImpl> {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    private final List<LobbyListener> listeners = new CopyOnWriteArrayList<>();
    private final MessageListener messageListener = new MessageListener();
    private XMPPTCPConnection connection;
    private Roster roster;

    /**
     * Initialize this implementation
     *
     * @param connection the XMPP connection
     */
    public LobbyResourceImpl(final XMPPTCPConnection connection) {
        this.connection = connection;
        this.roster = Roster.getInstanceFor(connection);
        connection.setParsingExceptionCallback(messageListener);
    }

    @Override
    public Lobby getLobby(final long userId) {
        final var presence = roster.getPresence(JidCreate.bareFromOrThrowUnchecked(userId + "@" + Origin.CHAT_DOMAIN));
        if (presence.getType() == Presence.Type.available) {
            final var activity = (ActivityExtension) presence.getExtension(ActivityExtension.NAMESPACE);
            return new Lobby(activity.getText());
        }
        return null;
    }

    @Override
    public Lobby getLobby(final Jid jid) {
        final var presence = roster.getPresence(jid.asBareJid());
        if (presence.getType() == Presence.Type.available) {
            final var activity = (ActivityExtension) presence.getExtension(ActivityExtension.NAMESPACE);
            return new Lobby(activity.getText());
        }
        return null;
    }

    @Override
    public void addLobbyListener(final LobbyListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeLobbyListener(final LobbyListener listener) {
        listeners.remove(listener);
    }

    @Override
    public LobbyResourceImpl reinitialize(final XMPPTCPConnection connection) {
        this.connection = connection;
        connection.setParsingExceptionCallback(messageListener);
        this.roster = Roster.getInstanceFor(connection);
        return this;
    }

    @Override
    public void clear() {
        connection.setParsingExceptionCallback(null);
    }

    @Override
    public void dispose() {
        connection.setParsingExceptionCallback(null);
    }

    /**
     * Handles getting the invite message.
     */
    private final class MessageListener implements ParsingExceptionCallback {
        @Override
        public void handleUnparsableStanza(UnparseableStanza stanzaData) {
            try {
                final var factory = XmlPullParserFactory.newInstance();
                final var xml = factory.newPullParser();
                xml.setInput(new StringReader(stanzaData.getContent().toString()));

                xml.next();
                xml.next();
                xml.nextText();

                xml.next();
                final var link = xml.nextText();
                xml.next();
                final var id = xml.nextText();
                final var userId = Long.parseLong(link.split("_")[2]);

                listeners.forEach(listener -> listener.onInvitation(link, id, userId));
            } catch (final XmlPullParserException | IOException | ArrayIndexOutOfBoundsException | NumberFormatException exception) {
                LOGGER.atWarning().withCause(exception).log("Failed to parse XML. data={" + stanzaData.getContent() + "}");
            }
        }
    }

}
