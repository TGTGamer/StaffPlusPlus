package net.shortninja.staffplus.staff.ticketing;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.exceptions.BusinessException;
import net.shortninja.staffplus.common.exceptions.NoPermissionException;
import net.shortninja.staffplus.event.tickets.CloseTicketEvent;
import net.shortninja.staffplus.event.tickets.ReopenTicketEvent;
import net.shortninja.staffplus.event.tickets.TicketStatus;
import net.shortninja.staffplus.player.PlayerManager;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.staff.ticketing.database.TicketRepository;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getScheduler;

public class ManageTicketService {

    private static final Map<UUID, Long> lastUse = new HashMap<UUID, Long>();

    private final PermissionHandler permission = IocContainer.getPermissionHandler();
    private final MessageCoordinator message = IocContainer.getMessage();
    private final Options options = IocContainer.getOptions();
    private final Messages messages;
    private final PlayerManager playerManager;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    public ManageTicketService(TicketRepository ticketRepository, Messages messages, PlayerManager playerManager, TicketService ticketService) {
        this.ticketRepository = ticketRepository;
        this.messages = messages;
        this.playerManager = playerManager;
        this.ticketService = ticketService;
    }

    public void acceptTicket(Player player, int ticketId) {
        if(!permission.has(player, options.manageTicketsConfiguration.getPermissionReject())) {
            throw new NoPermissionException();
        }

        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
            Ticket ticket = ticketRepository.findOpenTicket(ticketId)
                .orElseThrow(() -> new BusinessException("Ticket with id [" + ticketId + "] not found", messages.prefixTickets));

            ticket.setTicketStatus(TicketStatus.IN_PROGRESS);
            ticket.setStaffUuid(player.getUniqueId());
            ticket.setStaffName(player.getName());
            ticketRepository.updateTicket(ticket);
            message.sendGroupMessage(player.getName() + " accepted ticket from " + ticket.getCreatorName(), options.permissionTicketUpdateNotifications, messages.prefixTickets);
            sendEvent(new CloseTicketEvent(ticket));
        });

    }

    public void reopenTicket(Player player, int ticketId) {
        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
            Ticket ticket = ticketService.getTicket(ticketId);
            if (!ticket.getStaffUuid().equals(player.getUniqueId())) {
                throw new BusinessException("&CYou cannot change the status of a ticket you are not assigned to", messages.prefixTickets);
            }

            ticket.setStaffUuid(null);
            ticket.setStaffName(null);
            ticket.setTicketStatus(TicketStatus.OPEN);
            ticketRepository.updateTicket(ticket);
            message.sendGroupMessage(player.getName() + " reopened ticket from " + ticket.getCreatorName(), options.permissionTicketUpdateNotifications, messages.prefixTickets);
            sendEvent(new ReopenTicketEvent(ticket));
        });
    }

    public void closeTicket(Player player, CloseTicketRequest closeTicketRequest) {
        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
            Ticket ticket = ticketService.getTicket(closeTicketRequest.getTicketId());
            closedTicket(player, ticket, closeTicketRequest.getStatus(), closeTicketRequest.getCloseReason());
            message.sendGroupMessage(player.getName() + " changed ticket status to " + closeTicketRequest.getStatus() + ". Creator: " + ticket.getCreatorName(), options.permissionTicketUpdateNotifications, messages.prefixTickets);
            sendEvent(new CloseTicketEvent(ticket));
        });
    }

    private void closedTicket(Player player, Ticket ticket, TicketStatus status, String closeReason) {
        if (!ticket.getStaffUuid().equals(player.getUniqueId())) {
            throw new BusinessException("&CYou cannot change the status of a ticket you are not assigned to", messages.prefixTickets);
        }

        if(status == TicketStatus.REJECTED && !permission.has(player, options.manageTicketsConfiguration.getPermissionReject())) {
            throw new NoPermissionException();
        }
        if(status == TicketStatus.RESOLVED && !permission.has(player, options.manageTicketsConfiguration.getPermissionResolve())) {
            throw new NoPermissionException();
        }
        ticket.setTicketStatus(status);
        ticket.setCloseReason(closeReason);
        ticketRepository.updateTicket(ticket);
    }

    public List<Ticket> getClosedTickets(int offset, int amount) {
        return ticketRepository.getClosedTickets(offset, amount);
    }

    private void sendEvent(Event event) {
        getScheduler().runTask(StaffPlus.get(), () -> {
            Bukkit.getPluginManager().callEvent(event);
        });
    }

    public void deleteTicket(Player player, int ticketId) {
        if(!permission.has(player, options.manageTicketsConfiguration.getPermissionDelete())) {
            throw new NoPermissionException();
        }
        Ticket ticket = ticketService.getTicket(ticketId);
        ticketRepository.markTicketDeleted(ticket);
        message.sendGroupMessage(player.getName() + " deleted ticket from " + ticket.getCreatorName(), options.permissionTicketUpdateNotifications, messages.prefixTickets);
    }
}
