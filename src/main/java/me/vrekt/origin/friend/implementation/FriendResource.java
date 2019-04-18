package me.vrekt.origin.friend.implementation;

import me.vrekt.origin.friend.listener.FriendListener;
import org.jxmpp.jid.Jid;

import java.util.Set;

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
     * @return {@code true} if the operation was successful.
     */
    boolean sendFriendRequest(final Long userId);

    /**
     * Attempts to remove the friend.
     *
     * @param userId their user ID.
     * @return {@code true} if the operation was successful.
     */
    boolean removeFriend(final Long userId);

    /**
     * Attempts to add the friend.
     *
     * @param userId their user ID.
     * @return {@code true} if the operation was successful.
     */
    boolean acceptFriendRequest(final Long userId);

    /**
     * Accept a friend request by JID.
     *
     * @param user the user
     * @return {@code true} if the operation was successful.
     */
    boolean acceptFriendRequest(final Jid user);

    /**
     * @param userId the user ID of who to check
     * @return {@code true} if the provided {@code userId} is friends with the current account
     */
    boolean isFriendWith(final Long userId);

    /**
     * @param user the user JID of who to check
     * @return {@code true} if the provided {@code user} is friends with the current account
     */
    boolean isFriendWith(final Jid user);

    /**
     * @return a {@link Set} that will contain all friends by their ID.
     */
    Set<Long> getFriendsById();

    /**
     * @return a {@link Set} that will contain all friends by their origin persona ID.
     */
    Set<Long> getFriendsByPersonaId();

    /**
     * @return a {@link Set} that will contain all friends by their JID
     */
    Set<Jid> getFriendsByJid();

    /**
     * @return a {@link Set} that will contain all pending requests by JID.
     */
    Set<Jid> getAllPendingByJid();

    /**
     * @return a {@link Set} that will contain pending requests by their origin persona ID.
     */
    Set<Long> getAllPendingByPersonaId();
}
