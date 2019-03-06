package me.vrekt.origin.exception;

/**
 * A class used for general exceptions:
 * Ranging from {@link java.io.IOException}, {@link org.jivesoftware.smack.SmackException.NotConnectedException}, {@link InterruptedException}
 */
public final class OriginException extends RuntimeException {
    public OriginException(String message, Throwable cause) {
        super(message, cause);
    }
}
