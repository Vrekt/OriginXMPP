package me.vrekt.origin.chat.implementation.message;

import me.vrekt.origin.exception.OriginException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jxmpp.jid.EntityBareJid;

public final class Message {

    private final EntityBareJid from;
    private final String message;

    private final Chat chat;

    /**
     * Initialize this message
     *
     * @param from    who the message is from.
     * @param message the content of the message.
     * @param chat    the chat instance.
     */
    public Message(EntityBareJid from, String message, Chat chat) {
        this.from = from;
        this.message = message;
        this.chat = chat;
    }

    /**
     * @return the {@link EntityBareJid} of who it was sent from.
     */
    public EntityBareJid getFrom() {
        return from;
    }

    /**
     * @return the content of the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return smack's {@link Chat} instance.
     */
    public Chat getChat() {
        return chat;
    }

    /**
     * Attempts to reply to this message.
     *
     * @param message the message to send
     * @throws OriginException if {@link org.jivesoftware.smack.SmackException.NotConnectedException} or {@link InterruptedException} was thrown.
     */
    public void reply(String message) throws OriginException {
        try {
            chat.send(message);
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            throw new OriginException("Smack exception!", exception.getCause());
        }
    }

}
