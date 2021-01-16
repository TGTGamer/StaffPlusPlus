package net.shortninja.staffplus.staff.ticketing.gui;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.cmd.CommandUtil;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.player.attribute.gui.AbstractGui;
import net.shortninja.staffplus.player.attribute.gui.PagedGui;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.unordered.IAction;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OpenTicketsGui extends PagedGui {

    private final PermissionHandler permissionHandler;
    private final Options options;
    private final Supplier<AbstractGui> backGuiSupplier;

    public OpenTicketsGui(Player player, String title, int page, Supplier<AbstractGui> backGuiSupplier) {
        super(player, title, page, backGuiSupplier);
        this.backGuiSupplier = backGuiSupplier;
        permissionHandler = IocContainer.getPermissionHandler();
        options = IocContainer.getOptions();
    }

    @Override
    protected void getNextUi(Player player, SppPlayer target, String title, int page) {
        new OpenTicketsGui(player, title, page, backGuiSupplier);
    }

    @Override
    public IAction getAction() {
        return new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot) {
                if(!permissionHandler.has(player, options.manageTicketsConfiguration.getPermissionAccept())) {
                    return;
                }
                CommandUtil.playerAction(player, () -> {
                    int ticketId = Integer.parseInt(StaffPlus.get().versionProtocol.getNbtString(item));
                    IocContainer.getManageTicketService().acceptTicket(player, ticketId);
                });
            }

            @Override
            public boolean shouldClose(Player player) {
                return permissionHandler.has(player, options.manageTicketsConfiguration.getPermissionAccept());
            }
        };
    }

    @Override
    public List<ItemStack> getItems(Player player, SppPlayer target, int offset, int amount) {
        return IocContainer.getTicketService().getUnresolvedTickets(offset, amount).stream()
                .map(TicketItemBuilder::build)
                .collect(Collectors.toList());
    }
}