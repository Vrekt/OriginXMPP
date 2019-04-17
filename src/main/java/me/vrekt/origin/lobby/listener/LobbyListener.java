package me.vrekt.origin.lobby.listener;

public interface LobbyListener {

    /**
     * Invoked when a invitation received.
     *
     * @param joinLink the link to join (?)
     * @param inviteId the ID of the invite (?)
     * @param userId   whoever sent the invite
     */
    default void onInvitation(final String joinLink, final String inviteId, final Long userId) {

    }

}
