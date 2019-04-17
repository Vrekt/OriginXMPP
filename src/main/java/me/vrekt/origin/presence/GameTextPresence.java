package me.vrekt.origin.presence;

/**
 * A wrapper for presences, including status and activity.
 */
public final class GameTextPresence {

    private final String status, activity;

    public GameTextPresence(final String status, final String activity) {
        this.status = status;
        this.activity = activity;
    }

    public String status() {
        return status;
    }

    public String activity() {
        return activity;
    }
}
