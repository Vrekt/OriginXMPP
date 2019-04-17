package me.vrekt.origin.chat.implementation;

import com.google.common.flogger.FluentLogger;
import me.vrekt.origin.Origin;
import me.vrekt.origin.chat.listener.ChatListener;
import me.vrekt.origin.resource.ResourceInitializer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.util.concurrent.CopyOnWriteArrayList;

public final class ChatResourceImpl implements ChatResource, ResourceInitializer<ChatResourceImpl> {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    private final CopyOnWriteArrayList<ChatListener> listeners = new CopyOnWriteArrayList<>();
    private final MessageListener messageListener = new MessageListener();
    private ChatManager chatManager;

    /**
     * Initialize this implementation
     *
     * @param connection the XMPP connection
     */
    public ChatResourceImpl(final XMPPTCPConnection connection) {
        this.chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(messageListener);
    }

    @Override
    public void addChatListener(final ChatListener messageListener) {
        listeners.add(messageListener);
    }

    @Override
    public void removeChatListener(final ChatListener messageListener) {
        listeners.remove(messageListener);
    }

    @Override
    public void sendMessage(final Long userId, final String message) {
        final var to = JidCreate.entityBareFromOrThrowUnchecked(userId + "@" + Origin.CHAT_DOMAIN);
        sendMessage(to, message);
    }

    @Override
    public void sendMessage(final EntityBareJid user, final String message) {
        try {
            chatManager.chatWith(user).send(message);
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            LOGGER.atSevere().withCause(exception).log("Failed to send message! to={" + user.asUnescapedString() + "}, message={" + message + "}");
        }
    }


    @Override
    public ChatResourceImpl reinitialize(final XMPPTCPConnection connection) {
        this.chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(messageListener);
        return this;
    }

    @Override
    public void clear() {
        chatManager.removeIncomingListener(messageListener);
        chatManager = null;
    }

    @Override
    public void dispose() {
        clear();
        listeners.clear();
    }

    /**
     * A class used for listening for messages.
     */
    private final class MessageListener implements IncomingChatMessageListener {
        @Override
        public void newIncomingMessage(final EntityBareJid from, final Message message, final Chat chat) {
            if (message.getBody().isEmpty()) {
                listeners.forEach(listener -> listener.onTyping(from));
            } else {
                listeners.forEach(listener -> listener.onMessageReceived(new me.vrekt.origin.chat.message.Message(from, message.getBody(), chat)));
            }
        }
    }

}
