package net.shortninja.staffplus.staff.ticketing.gui;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.cmd.CommandUtil;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.player.attribute.gui.AbstractGui;
import net.shortninja.staffplus.player.attribute.gui.PagedGui;
import net.shortninja.staffplus.staff.ticketing.Ticket;
import net.shortninja.staffplus.unordered.IAction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AssignedTicketsGui extends PagedGui {

    public AssignedTicketsGui(Player player, String title, int page, Supplier<AbstractGui> previousGuiSupplier) {
        super(player, title, page, previousGuiSupplier);
    }

    @Override
    protected void getNextUi(Player player, SppPlayer target, String title, int page) {
        new AssignedTicketsGui(player, title, page, previousGuiSupplier);
    }

    @Override
    public IAction getAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot) {
                CommandUtil.playerAction(player, () -> {
                    int ticketId = Integer.parseInt(StaffPlus.get().versionProtocol.getNbtString(item));
                    Ticket ticket = IocContainer.getTicketService().getTicket(ticketId);
                    new ManageTicketGui(player, "Ticket by: " + ticket.getCreatorName(), ticket, () -> new AssignedTicketsGui(player, getTitle(), getCurrentPage(), getPreviousGuiSupplier()));
                });
            }

            @Override
            public boolean shouldClose(Player player) {
                return false;
            }
        };
    }

    @Override
    public List<ItemStack> getItems(Player player, SppPlayer target, int offset, int amount) {
        return IocContainer.getTicketService()
                .getAssignedTickets(player.getUniqueId(), offset, amount)
                .stream()
                .map(TicketItemBuilder::build)
                .collect(Collectors.toList());
    }
}