package net.shortninja.staffplus.server.listener.player;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManager;
import net.shortninja.staffplus.staff.tracing.TraceService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import static net.shortninja.staffplus.staff.tracing.TraceType.PICKUP_ITEM;

public class PlayerPickupItem implements Listener {
    private final Options options = IocContainer.getOptions();
    private final TraceService traceService = IocContainer.getTraceService();
    private final SessionManager sessionManager = IocContainer.getSessionManager();

    public PlayerPickupItem() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerSession playerSession = sessionManager.get(player.getUniqueId());

            if (options.modeItemChange || !playerSession.isInStaffMode()) {
                traceService.sendTraceMessage(PICKUP_ITEM, player.getUniqueId(), String.format("Picked up item [%s]", event.getItem().getItemStack().getType()));
                return;
            }
            event.setCancelled(true);
        }

    }
}