package me.vrekt.origin.lobby;

import me.vrekt.origin.lobby.privacy.PartyPrivacy;

/**
 * Represents an apex legends lobby.
 */
public final class Lobby {

    private String game, originLink, originLinkStripped, status;
    private PartyPrivacy privacy;

    /**
     * Initialize this lobby.
     *
     * @param input the input to parse
     */
    public Lobby(final String input) {
        if (input == null) return;
        // Apex Legends™;Origin.OFR.50.0002694;JOINABLE;;;194908;;;Lobby

        final var data = input.split(";");
        final var basic = input.split(";;;");
        if (data.length >= 2) {
            this.game = data[0];
            privacy = data[2].equalsIgnoreCase("JOINABLE") ? PartyPrivacy.OPEN : PartyPrivacy.INVITE;
            if (basic.length >= 2) {
                // Origin.OFR.50.0002694;JOINABLE;;;Apex Legends™;Origin.OFR.50.0002694;JOINABLE
                // Origin.OFR.50.0002694;JOINABLE;;;Lobby
                this.originLink = data[1] + ";" + data[2] + ";;;" + basic[1];
                this.originLinkStripped = originLink + ";;;";
            }
        }

        if (basic.length >= 2) {
            this.status = basic[1];
        }
    }

    /**
     * @return the current game, usually {@code "Apex Legends™"}
     */
    public String game() {
        return game;
    }

    /**
     * @return the origin link used for joining the game, usually looks like:
     * Origin.OFR.50.0002694;JOINABLE;;;194908;;;
     */
    public String originLink() {
        return originLink;
    }

    /**
     * @return the origin link without the ending ';;;'
     */
    public String originLinkStripped() {
        return originLinkStripped;
    }

    /**
     * @return the status, (lobby, playing, etc)
     */
    public String status() {
        return status;
    }

    /**
     * @return the privacy of the party
     */
    public PartyPrivacy privacy() {
        return privacy;
    }
}
