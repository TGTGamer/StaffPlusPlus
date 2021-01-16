package net.shortninja.staffplus.staff.ticketing;

import me.rayzr522.jsonmessage.JSONMessage;
import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.event.tickets.AcceptTicketEvent;
import net.shortninja.staffplus.event.tickets.CloseTicketEvent;
import net.shortninja.staffplus.event.tickets.TicketStatus;
import net.shortninja.staffplus.player.PlayerManager;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.unordered.ITicket;
import net.shortninja.staffplus.util.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;

public class TicketChangeCreatorNotifier implements Listener {

    private final Options options = IocContainer.getOptions();
    private final PlayerManager playerManager = IocContainer.getPlayerManager();
    private Permission permission = IocContainer.getPermissionHandler();

    public TicketChangeCreatorNotifier() {
        Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
    }



    @EventHandler(priority = EventPriority.NORMAL)
    public void handleAcceptTicket(AcceptTicketEvent event) {
        if (!options.ticketingConfiguration.getCreatorNotifyStatuses().contains(TicketStatus.IN_PROGRESS)) {
            return;
        }

        ITicket ticket = event.getTicket();
        Optional<SppPlayer> creator = playerManager.getOnlinePlayer(ticket.getCreatorUuid());
        creator.ifPresent(sppPlayer -> buildMessage(sppPlayer.getPlayer(), "Your ticket has been accepted by " + ticket.getStaffName()));
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void handleCloseTicket(CloseTicketEvent event) {
        if(event.getTicket().getTicketStatus() == TicketStatus.REJECTED) {
            handleRejectTicket(event);
        } else if (event.getTicket().getTicketStatus() == TicketStatus.RESOLVED) {
            handleResolveTicket(event);
        }
    }

    private void handleRejectTicket(CloseTicketEvent event) {
        if (!options.ticketingConfiguration.getCreatorNotifyStatuses().contains(TicketStatus.REJECTED)) {
            return;
        }

        ITicket ticket = event.getTicket();
        Optional<SppPlayer> creator = playerManager.getOnlinePlayer(ticket.getCreatorUuid());
        creator.ifPresent(sppPlayer -> buildMessage(sppPlayer.getPlayer(), "Your ticket has been rejected by " + ticket.getStaffName()));
    }

    private void handleResolveTicket(CloseTicketEvent event) {
        if (!options.ticketingConfiguration.getCreatorNotifyStatuses().contains(TicketStatus.RESOLVED)) {
            return;
        }

        ITicket ticket = event.getTicket();
        Optional<SppPlayer> creator = playerManager.getOnlinePlayer(ticket.getCreatorUuid());
        creator.ifPresent(sppPlayer -> buildMessage(sppPlayer.getPlayer(), "Your ticket has been resolved by " + ticket.getStaffName()));
    }

    private void buildMessage(Player player, String title) {
        JSONMessage message = JSONMessage.create(title)
            .color(ChatColor.GOLD);

        if (permission.has(player, options.ticketingConfiguration.getMyTicketsPermission())) {
            message.then(" View your tickets!")
                .color(ChatColor.BLUE)
                .tooltip("Click to view your tickets")
                .runCommand("/" + options.ticketingConfiguration.getMyTicketsCmd());
        }

        message.send(player);
    }
}
