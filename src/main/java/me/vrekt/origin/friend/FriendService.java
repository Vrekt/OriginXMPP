package me.vrekt.origin.friend;

import me.vrekt.origin.exception.OriginException;
import me.vrekt.origin.friend.implementation.FriendListener;

public interface FriendService extends AutoCloseable {

    /**
     * Adds a {@link FriendListener}
     *
     * @param listener the listener
     */
    void addFriendListener(FriendListener listener);

    /**
     * Removes a  {@link FriendListener}
     *
     * @param listener the listener
     */
    void removeFriendListener(FriendListener listener);

    /**
     * Attempts to send a friend request to the specified user.
     *
     * @param userId the ID of the user.
     * @throws OriginException if an {@link org.jivesoftware.smack.SmackException.NotConnectedException} or {@link InterruptedException} occurred.
     */
    void sendFriendRequest(Long userId) throws OriginException;

    /**
     * Attempts to remove the friend.
     *
     * @param userId their user ID.
     * @throws OriginException if an {@link org.jivesoftware.smack.SmackException.NotConnectedException} or {@link InterruptedException} occurred.
     */
    void removeFriend(Long userId) throws OriginException;

}
