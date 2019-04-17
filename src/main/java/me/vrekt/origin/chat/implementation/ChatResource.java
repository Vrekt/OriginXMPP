package me.vrekt.origin.chat.implementation;

import me.vrekt.origin.chat.listener.ChatListener;
import org.jxmpp.jid.EntityBareJid;

public interface ChatResource {

    /**
     * Add an {@link ChatListener}
     *
     * @param messageListener the listener
     */
    void addChatListener(final ChatListener messageListener);

    /**
     * Remove an {@link ChatListener}
     *
     * @param messageListener the listener
     */
    void removeChatListener(final ChatListener messageListener);

    /**
     * Attempts to send a message to the specified user ID.
     *
     * @param userId  the ID of the user
     * @param message the message
     */
    void sendMessage(final Long userId, final String message);

    /**
     * Attempts to send a message to the specified JID.
     *
     * @param user    the user.
     * @param message the message
     */
    void sendMessage(final EntityBareJid user, final String message);

}
