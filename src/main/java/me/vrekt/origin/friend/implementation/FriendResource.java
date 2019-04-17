package me.vrekt.origin.friend.implementation;

import me.vrekt.origin.friend.listener.FriendListener;

public interface FriendResource {

    /**
     * Adds a {@link FriendListener}
     *
     * @param listener the listener
     */
    void addFriendListener(final FriendListener listener);

    /**
     * Removes a  {@link FriendListener}
     *
     * @param listener the listener
     */
    void removeFriendListener(final FriendListener listener);

    /**
     * Attempts to send a friend request to the specified user.
     *
     * @param userId the ID of the user.
     */
    boolean sendFriendRequest(final Long userId);

    /**
     * Attempts to remove the friend.
     *
     * @param userId their user ID.
     */
    boolean removeFriend(final Long userId);

    /**
     * Attempts to add the friend.
     *
     * @param userId their user ID.
     */
    boolean acceptFriendRequest(final Long userId);

}
