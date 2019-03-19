package me.vrekt.origin.chat;

import me.vrekt.origin.chat.implementation.ChatListener;
import me.vrekt.origin.exception.OriginException;
import org.jxmpp.jid.EntityBareJid;

public interface ChatService extends AutoCloseable {

    /**
     * Add an {@link ChatListener}
     *
     * @param messageListener the listener
     */
    void addChatListener(ChatListener messageListener);

    /**
     * Remove an {@link ChatListener}
     *
     * @param messageListener the listener
     */
    void removeChatListener(ChatListener messageListener);

    /**
     * Attempts to send a message to the specified user ID.
     *
     * @param userId  the ID of the user
     * @param message the message
     * @throws OriginException if an {@link org.jivesoftware.smack.SmackException.NotConnectedException} or {@link InterruptedException} occurred.
     */
    void sendMessage(Long userId, String message) throws OriginException;

    /**
     * Attempts to send a message to the specified JID.
     *
     * @param user    the user.
     * @param message the message
     * @throws OriginException if an {@link org.jivesoftware.smack.SmackException.NotConnectedException} or {@link InterruptedException} occurred.
     */
    void sendMessage(EntityBareJid user, String message) throws OriginException;

}
