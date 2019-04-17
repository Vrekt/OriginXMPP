package me.vrekt.origin.lobby.implementation;

import me.vrekt.origin.lobby.Lobby;
import me.vrekt.origin.lobby.listener.LobbyListener;
import me.vrekt.origin.lobby.privacy.PartyPrivacy;
import org.jxmpp.jid.Jid;

public interface LobbyResource {

    /**
     * Get thr privacy of a lobby, this returns {@code null} if the privacy settings are invalid or they have no lobby.
     *
     * @param userId the user ID
     * @return a new {@link PartyPrivacy} if valid, {@code null} otherwise
     */
    Lobby getLobby(final long userId);

    /**
     * Get thr privacy of a lobby, this returns {@code null} if the privacy settings are invalid or they have no lobby.
     *
     * @param jid the jid of who to check
     * @return a new {@link PartyPrivacy} if valid, {@code null} otherwise
     */
    Lobby getLobby(final Jid jid);

    /**
     * Add a new {@link LobbyListener}
     *
     * @param listener the listener
     */
    void addLobbyListener(final LobbyListener listener);

    /**
     * Adds a new {@link LobbyListener}
     *
     * @param listener the listener
     */
    void removeLobbyListener(final LobbyListener listener);

}
