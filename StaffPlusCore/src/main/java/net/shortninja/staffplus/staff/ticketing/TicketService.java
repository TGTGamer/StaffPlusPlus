package net.shortninja.staffplus.staff.ticketing;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.exceptions.BusinessException;
import net.shortninja.staffplus.event.tickets.CreateTicketEvent;
import net.shortninja.staffplus.event.tickets.TicketStatus;
import net.shortninja.staffplus.player.PlayerManager;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.staff.ticketing.database.TicketRepository;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.time.ZonedDateTime;
import java.util.*;

import static org.bukkit.Bukkit.getScheduler;

public class TicketService {

    private static final Map<UUID, Long> lastUse = new HashMap<UUID, Long>();

    private final PermissionHandler permission = IocContainer.getPermissionHandler();
    private final MessageCoordinator message = IocContainer.getMessage();
    private final Options options = IocContainer.getOptions();
    private final Messages messages;
    private final PlayerManager playerManager;
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository, Messages messages, PlayerManager playerManager) {
        this.ticketRepository = ticketRepository;
        this.messages = messages;
        this.playerManager = playerManager;
    }

    public void createTicket(CommandSender sender, String description) {
        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
            validateCoolDown(sender);

            String creatorName = sender instanceof Player ? sender.getName() : "Console";
            UUID creatorUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : StaffPlus.get().consoleUUID;
            Ticket ticket = new Ticket(
                description,
                creatorName,
                creatorUuid,
                TicketStatus.OPEN,
                ZonedDateTime.now());

            int id = ticketRepository.addTicket(ticket);
            ticket.setId(id);

            message.send(sender, messages.ticketCreated.replace("%id%", ticket.getId() + "").replace("%creator%", ticket.getCreatorName()).replace("%description%", ticket.getDescription()), messages.prefixTickets);
            message.sendGroupMessage(messages.ticketCreateStaff.replace("%id%", ticket.getId() + "").replace("%creator%", ticket.getCreatorName()).replace("%description%", ticket.getDescription()), options.permissionTicketUpdateNotifications, messages.prefixTickets);
            options.ticketingConfiguration.getSound().playForGroup(options.permissionTicketUpdateNotifications);

            if (sender instanceof Player) {
                lastUse.put(creatorUuid, System.currentTimeMillis());
            }
            sendEvent(new CreateTicketEvent(ticket));
        });
    }

    public Collection<Ticket> getUnresolvedTickets(int offset, int amount) {
        return ticketRepository.getUnresolvedTickets(offset, amount);
    }

    public Collection<Ticket> getAssignedTickets(UUID staffUuid, int offset, int amount) {
        return ticketRepository.getAssignedTickets(staffUuid, offset, amount);
    }

    public Collection<Ticket> getMyTickets(UUID creatorUuid, int offset, int amount) {
        return ticketRepository.getMyTickets(creatorUuid, offset, amount);
    }

    public List<Ticket> getMyTickets(UUID creatorUuid) {
        return ticketRepository.getMyTickets(creatorUuid);
    }

    private void validateCoolDown(CommandSender sender) {
        long last = sender instanceof Player ? (lastUse.containsKey(((Player) sender).getUniqueId()) ? lastUse.get(((Player) sender).getUniqueId()) : 0) : 0;
        long remaining = (System.currentTimeMillis() - last) / 1000;

        if (remaining < options.ticketingConfiguration.getCooldown()) {
            throw new BusinessException(messages.commandOnCooldown.replace("%seconds%", Long.toString(options.ticketingConfiguration.getCooldown() - remaining)), messages.prefixGeneral);
        }
    }

    private SppPlayer getUser(UUID playerUuid) {
        Optional<SppPlayer> player = playerManager.getOnOrOfflinePlayer(playerUuid);
        if (!player.isPresent()) {
            throw new BusinessException(messages.playerNotRegistered, messages.prefixGeneral);
        }
        return player.get();
    }

    public Ticket getTicket(int ticketId) {
        return ticketRepository.findTicket(ticketId)
            .orElseThrow(() -> new BusinessException("Ticket with id [" + ticketId + "] not found", messages.prefixTickets));
    }

    private void sendEvent(Event event) {
        getScheduler().runTask(StaffPlus.get(), () -> {
            Bukkit.getPluginManager().callEvent(event);
        });
    }
}
