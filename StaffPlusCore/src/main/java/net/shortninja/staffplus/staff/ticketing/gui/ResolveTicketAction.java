package net.shortninja.staffplus.staff.ticketing.gui;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.event.tickets.TicketStatus;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManager;
import net.shortninja.staffplus.staff.ticketing.CloseTicketRequest;
import net.shortninja.staffplus.staff.ticketing.ManageTicketService;
import net.shortninja.staffplus.unordered.IAction;
import net.shortninja.staffplus.util.MessageCoordinator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ResolveTicketAction implements IAction {
    private static final String CANCEL = "cancel";
    private final Messages messages = IocContainer.getMessages();
    private final MessageCoordinator messageCoordinator = IocContainer.getMessage();
    private final SessionManager sessionManager = IocContainer.getSessionManager();
    private final ManageTicketService manageTicketService = IocContainer.getManageTicketService();
    private final Options options = IocContainer.getOptions();

    @Override
    public void click(Player player, ItemStack item, int slot) {

        int ticketId = Integer.parseInt(StaffPlus.get().versionProtocol.getNbtString(item));
        if(options.ticketingConfiguration.isClosingReasonEnabled()) {
            messageCoordinator.send(player, "&1===================================================", messages.prefixTickets);
            messageCoordinator.send(player, "&6       You have chosen to resolve this ticket", messages.prefixTickets);
            messageCoordinator.send(player, "&6Type your closing reason in chat to resolve the ticket", messages.prefixTickets);
            messageCoordinator.send(player, "&6      Type \"cancel\" to cancel closing the ticket ", messages.prefixTickets);
            messageCoordinator.send(player, "&1===================================================", messages.prefixTickets);
            PlayerSession playerSession = sessionManager.get(player.getUniqueId());
            playerSession.setChatAction((player1, message) -> {
                if (message.equalsIgnoreCase(CANCEL)) {
                    messageCoordinator.send(player, "&CYou have cancelled rejecting this ticket", messages.prefixTickets);
                    return;
                }
                manageTicketService.closeTicket(player, new CloseTicketRequest(ticketId, TicketStatus.RESOLVED, message));
            });
        } else {
            manageTicketService.closeTicket(player, new CloseTicketRequest(ticketId, TicketStatus.RESOLVED, null));
        }
    }

    @Override
    public boolean shouldClose(Player player) {
        return true;
    }
}
