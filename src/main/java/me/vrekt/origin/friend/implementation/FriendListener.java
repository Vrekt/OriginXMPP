package me.vrekt.origin.friend.implementation;

public interface FriendListener {

    /**
     * Invoked when a new friend request is received.
     *
     * @param userId the ID of the user.
     */
    default void onFriendRequestReceived(Long userId) {

    }

    /**
     * Invoked when a friend request was accepted
     *
     * @param userId the ID of the user.
     */
    default void onFriendRequestAccepted(Long userId) {

    }

}
