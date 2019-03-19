package me.vrekt.origin.chat.implementation;

import me.vrekt.origin.chat.implementation.message.Message;
import org.jxmpp.jid.EntityBareJid;

public interface ChatListener {

    /**
     * Invoked when a message is received.
     *
     * @param message the message
     */
    void onMessageReceived(Message message);

    /**
     * Invoked when somebody starts typing in the chat box.
     *
     * @param from who it was from.
     */
    default void onTyping(EntityBareJid from) {

    }

}
