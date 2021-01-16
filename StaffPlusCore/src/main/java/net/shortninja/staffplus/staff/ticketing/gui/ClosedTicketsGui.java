package net.shortninja.staffplus.staff.ticketing.gui;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.player.attribute.gui.AbstractGui;
import net.shortninja.staffplus.player.attribute.gui.PagedGui;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.staff.ticketing.Ticket;
import net.shortninja.staffplus.unordered.IAction;
import net.shortninja.staffplus.util.Permission;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ClosedTicketsGui extends PagedGui {

    private Permission permission = IocContainer.getPermissionHandler();
    private Options options = IocContainer.getOptions();

    public ClosedTicketsGui(Player player, String title, int page, Supplier<AbstractGui> backGuiSupplier) {
        super(player, title, page, backGuiSupplier);
    }

    @Override
    protected void getNextUi(Player player, SppPlayer target, String title, int page) {
        new ClosedTicketsGui(player, title, page, this.previousGuiSupplier);
    }

    @Override
    public IAction getAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot) {
                if (permission.has(player, options.manageTicketsConfiguration.getPermissionDelete())) {
                    int ticketId = Integer.parseInt(StaffPlus.get().versionProtocol.getNbtString(item));
                    Ticket ticket = IocContainer.getTicketService().getTicket(ticketId);
                    new ClosedTicketManageGui(player, "Manage closed ticket", ticket);
                }
            }

            @Override
            public boolean shouldClose(Player player) {
                return false;
            }
        };
    }

    @Override
    public List<ItemStack> getItems(Player player, SppPlayer target, int offset, int amount) {
        return IocContainer.getManageTicketService().getClosedTickets(offset, amount)
            .stream()
            .map(TicketItemBuilder::build)
            .collect(Collectors.toList());
    }
}