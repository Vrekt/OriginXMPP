package me.vrekt.origin.chat.implementation;

import me.vrekt.origin.Origin;
import me.vrekt.origin.chat.ChatService;
import me.vrekt.origin.exception.OriginException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultChatService implements ChatService {

    private final CopyOnWriteArrayList<ChatListener> listeners = new CopyOnWriteArrayList<>();
    private final MessageListener messageListener = new MessageListener();
    private final ChatManager chatManager;

    public DefaultChatService(XMPPTCPConnection connection) {
        this.chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(messageListener);
    }

    @Override
    public void addChatListener(ChatListener messageListener) {
        listeners.add(messageListener);
    }

    @Override
    public void removeChatListener(ChatListener messageListener) {
        listeners.remove(messageListener);
    }

    @Override
    public void sendMessage(Long userId, String message) throws OriginException {
        final var to = JidCreate.entityBareFromOrThrowUnchecked(userId + "@" + Origin.CHAT_DOMAIN);
        sendMessage(to, message);
    }

    @Override
    public void sendMessage(EntityBareJid user, String message) throws OriginException {
        try {
            chatManager.chatWith(user).send(message);
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            throw new OriginException("Smack exception!", exception.getCause());
        }
    }

    @Override
    public void close() {
        chatManager.removeIncomingListener(messageListener);
        listeners.clear();
    }

    private final class MessageListener implements IncomingChatMessageListener {
        @Override
        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
            if (message.getBody().isEmpty()) {
                listeners.forEach(listener -> listener.onTyping(from));
            } else {
                listeners.forEach(listener -> listener.onMessageReceived(new me.vrekt.origin.chat.implementation.message.Message(from, message.getBody(), chat)));
            }
        }
    }

}
