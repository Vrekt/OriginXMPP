package me.vrekt.origin.chat.message;

import com.google.common.flogger.FluentLogger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jxmpp.jid.EntityBareJid;

public final class Message {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

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
     * @return the account ID of whoever sent this message
     */
    public long accountId() {
        return Long.valueOf(from.getLocalpartOrThrow().asUnescapedString());
    }

    /**
     * Attempts to reply to this message.
     *
     * @param message the message to send
     */
    public boolean reply(String message) {
        try {
            chat.send(message);
            return true;
        } catch (final SmackException.NotConnectedException | InterruptedException exception) {
            LOGGER.atSevere().withCause(exception).log("Could not reply to: " + accountId() + " with message: " + message);
        }
        return false;
    }

}
