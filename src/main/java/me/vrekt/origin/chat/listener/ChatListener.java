package me.vrekt.origin.chat.listener;

import me.vrekt.origin.chat.message.Message;
import org.jxmpp.jid.EntityBareJid;

public interface ChatListener {

    /**
     * Invoked when a message is received.
     *
     * @param message the message
     */
    default void onMessageReceived(final Message message) {

    }

    /**
     * Invoked when somebody starts typing in the chat box.
     *
     * @param from who it was from.
     */
    default void onTyping(final EntityBareJid from) {

    }

}
