package me.vrekt.origin.chat.implementation;

import me.vrekt.origin.chat.implementation.message.Message;

public interface IncomingMessageListener {

    /**
     * Invoked when a message is received.
     *
     * @param message the message
     */
    void onMessageReceived(Message message);

}
