package me.vrekt.origin.friend.listener;

public interface FriendListener {

    /**
     * Invoked when a new friend request is received.
     *
     * @param userId the ID of the user.
     */
    default void onFriendRequestReceived(final Long userId) {

    }

    /**
     * Invoked when a friend request was accepted
     *
     * @param userId the ID of the user.
     */
    default void onFriendRequestAccepted(final Long userId) {

    }

}
