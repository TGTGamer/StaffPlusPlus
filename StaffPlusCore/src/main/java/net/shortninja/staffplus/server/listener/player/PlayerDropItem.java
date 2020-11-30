package net.shortninja.staffplus.server.listener.player;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManager;
import net.shortninja.staffplus.staff.freeze.FreezeHandler;
import net.shortninja.staffplus.staff.tracing.TraceService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.UUID;

import static net.shortninja.staffplus.staff.tracing.TraceType.DROP_ITEM;

public class PlayerDropItem implements Listener {
    private final Options options = IocContainer.getOptions();
    private final FreezeHandler freezeHandler = IocContainer.getFreezeHandler();
    private final SessionManager sessionManager = IocContainer.getSessionManager();
    private final TraceService traceService = IocContainer.getTraceService();

    public PlayerDropItem() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerSession playerSession = sessionManager.get(uuid);
        if ((options.modeItemChange || !playerSession.isInStaffMode()) && !freezeHandler.isFrozen(uuid)) {
            traceService.sendTraceMessage(DROP_ITEM, event.getPlayer().getUniqueId(), String.format("Dropped item [%s]", event.getItemDrop().getType()));
            return;
        }

        event.setCancelled(true);
    }
}