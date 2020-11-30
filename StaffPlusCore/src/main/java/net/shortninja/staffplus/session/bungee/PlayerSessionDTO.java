package net.shortninja.staffplus.session.bungee;

import net.shortninja.staffplus.unordered.VanishType;

import java.io.Serializable;
import java.util.UUID;

public class PlayerSessionDTO implements Serializable {

    private UUID playerUuid;
    private boolean staffModeEnabled;
    private VanishType vanishType;

    public PlayerSessionDTO(UUID playerUuid, boolean staffModeEnabled, VanishType vanishType) {
        this.playerUuid = playerUuid;
        this.staffModeEnabled = staffModeEnabled;
        this.vanishType = vanishType;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public boolean isStaffModeEnabled() {
        return staffModeEnabled;
    }

    public VanishType getVanishType() {
        return vanishType;
    }
}
