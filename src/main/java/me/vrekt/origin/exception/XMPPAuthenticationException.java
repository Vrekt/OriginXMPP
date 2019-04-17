package me.vrekt.origin.exception;

/**
 * This exception is thrown when an attempt to connect to the XMPP service failed.
 */
public final class XMPPAuthenticationException extends Exception {

    public XMPPAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
