package me.vrekt.origin.presence.implementation;

import me.vrekt.origin.presence.GameTextPresence;
import org.jivesoftware.smack.packet.Presence;

/**
 * This resource handles presence related things.
 */
public interface PresenceResource {

    /**
     * Set your presence to the provided {@code presence}
     *
     * @param presence the presence to set.
     */
    void setGameTextPresence(final GameTextPresence presence);

    /**
     * Set your presence to the status and activity provided.
     *
     * @param status   the status
     * @param activity the current activity
     */
    void setRawTextPresence(final String status, final String activity);

    /**
     * Set a direct presence.
     *
     * @param presence the presence
     */
    void setPresence(final Presence presence);

}
