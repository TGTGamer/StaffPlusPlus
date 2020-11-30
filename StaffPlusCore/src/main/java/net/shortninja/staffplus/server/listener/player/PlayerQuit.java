package net.shortninja.staffplus.server.listener.player;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.staff.mode.StaffModeService;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.SessionManager;
import net.shortninja.staffplus.staff.tracing.TraceService;
import net.shortninja.staffplus.staff.vanish.VanishHandler;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.factory.InventoryFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    private final MessageCoordinator message = IocContainer.getMessage();
    private final Options options = IocContainer.getOptions();
    private final Messages messages = IocContainer.getMessages();
    private final SessionManager sessionManager = IocContainer.getSessionManager();
    private final StaffModeService staffModeService = IocContainer.getStaffModeService();
    private final VanishHandler vanishHandler = IocContainer.getVanishHandler();
    private final TraceService traceService = IocContainer.getTraceService();

    public PlayerQuit() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        StaffPlus.get().versionProtocol.uninject(event.getPlayer());

        Player player = event.getPlayer();
        PlayerSession playerSession = sessionManager.get(player.getUniqueId());

        manageUser(player, playerSession);
        staffModeService.removeMode(player);
        vanishHandler.removeVanish(player);
        if (playerSession.isFrozen()) {
            for (String command : options.logoutCommands) {
                command = command.replace("%player%", player.getName());
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
        if (options.enderOfflineChestEnabled && !InventoryFactory.isInventoryEmpty(event.getPlayer().getEnderChest())) {
            InventoryFactory.saveEnderChest(event.getPlayer());
        }

        traceService.sendTraceMessage(player.getUniqueId(), "Left the game");
        traceService.stopAllTracesForPlayer(player.getUniqueId());
    }

    private void manageUser(Player player, PlayerSession session) {
        if (session.isFrozen()) {
            message.sendGroupMessage(messages.freezeLogout.replace("%player%", player.getName()), options.permissionFreeze, messages.prefixGeneral);
        }
        sessionManager.unload(player.getUniqueId());
    }
}