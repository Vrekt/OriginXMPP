package me.vrekt.origin.resource;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * An interface that all resources should implement to either:
 * A re-initialize the resource
 * B prepare this resource to be re-initialized
 * B dispose of the resource
 */
public interface ResourceInitializer<T> {

    /**
     * Re-Initialize this resource
     *
     * @param connection the  {@link XMPPTCPConnection} instance
     * @return the current instance
     */
    T reinitialize(final XMPPTCPConnection connection);

    /**
     * Clear this resource of its resources.
     */
    void clear();

    /**
     * Dispose of this resource
     */
    void dispose();

}
