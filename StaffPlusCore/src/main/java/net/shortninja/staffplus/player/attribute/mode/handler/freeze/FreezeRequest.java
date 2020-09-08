package net.shortninja.staffplus.player.attribute.mode.handler.freeze;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeRequest {

    private CommandSender commandSender;
    private Player player;
    private boolean enableFreeze;
    private String teleportLocation;

    public FreezeRequest(CommandSender commandSender, Player player, boolean enableFreeze, String teleportLocation) {
        this.commandSender = commandSender;
        this.player = player;
        this.enableFreeze = enableFreeze;
        this.teleportLocation = teleportLocation;
    }

    public FreezeRequest(CommandSender commandSender, Player player, boolean enableFreeze) {
        this.commandSender = commandSender;
        this.player = player;
        this.enableFreeze = enableFreeze;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isEnableFreeze() {
        return enableFreeze;
    }

    public String getTeleportLocation() {
        return teleportLocation;
    }
}