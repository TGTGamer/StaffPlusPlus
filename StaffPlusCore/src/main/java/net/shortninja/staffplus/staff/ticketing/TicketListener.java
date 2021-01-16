package net.shortninja.staffplus.staff.ticketing;

import me.rayzr522.jsonmessage.JSONMessage;
import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.util.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.stream.Collectors;

public class TicketListener implements Listener {

    private final TicketService ticketService = IocContainer.getTicketService();
    private final Options options = IocContainer.getOptions();
    private final Permission permission = IocContainer.getPermissionHandler();

    public TicketListener() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void notifyTickets(PlayerJoinEvent event) {
        if (!options.ticketingConfiguration.isNotifyCreatorOnJoin()) {
            return;
        }
        List<Ticket> tickets = ticketService.getMyTickets(event.getPlayer().getUniqueId());
        List<Ticket> openTickets = tickets.stream().filter(r -> !r.getTicketStatus().isClosed()).collect(Collectors.toList());

        if (openTickets.size() > 0) {
            JSONMessage message = JSONMessage.create("You have " + openTickets.size() + " open tickets")
                .color(ChatColor.GOLD);

            if (permission.has(event.getPlayer(), options.ticketingConfiguration.getMyTicketsPermission())) {
                message.then(" View your tickets!")
                    .color(ChatColor.BLUE)
                    .tooltip("Click to view your tickets")
                    .runCommand("/" + options.ticketingConfiguration.getMyTicketsCmd());
            }
            message.send(event.getPlayer());
        }
    }
}
