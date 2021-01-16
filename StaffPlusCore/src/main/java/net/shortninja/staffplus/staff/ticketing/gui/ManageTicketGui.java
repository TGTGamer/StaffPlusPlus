package net.shortninja.staffplus.staff.ticketing.gui;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.cmd.CommandUtil;
import net.shortninja.staffplus.player.attribute.gui.AbstractGui;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.SessionManager;
import net.shortninja.staffplus.staff.ticketing.ManageTicketService;
import net.shortninja.staffplus.staff.ticketing.Ticket;
import net.shortninja.staffplus.unordered.IAction;
import net.shortninja.staffplus.util.Permission;
import net.shortninja.staffplus.util.lib.hex.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class ManageTicketGui extends AbstractGui {
    private static final int SIZE = 54;

    private final SessionManager sessionManager = IocContainer.getSessionManager();
    private final ManageTicketService manageTicketService = IocContainer.getManageTicketService();
    private final Permission permission = IocContainer.getPermissionHandler();
    private final Options options = IocContainer.getOptions();

    public ManageTicketGui(Player player, String title, Ticket ticket, Supplier<AbstractGui> previousGuiSupplier) {
        super(SIZE, title, previousGuiSupplier);


        IAction reopenAction = new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot) {
                CommandUtil.playerAction(player, () -> {
                    int ticketId = Integer.parseInt(StaffPlus.get().versionProtocol.getNbtString(item));
                    manageTicketService.reopenTicket(player, ticketId);
                });
            }

            @Override
            public boolean shouldClose(Player player) {
                return true;
            }
        };

        IAction resolveAction = new ResolveTicketAction();
        IAction rejectAction = new RejectTicketAction();
        IAction deleteAction = new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot) {
                CommandUtil.playerAction(player, () -> {
                    int ticketId = Integer.parseInt(StaffPlus.get().versionProtocol.getNbtString(item));
                    manageTicketService.deleteTicket(player, ticketId);
                });
            }

            @Override
            public boolean shouldClose(Player player) {
                return true;
            }
        };

        setItem(13, TicketItemBuilder.build(ticket), null);

        if(permission.has(player, options.manageTicketsConfiguration.getPermissionResolve())) {
            addResolveItem(ticket, resolveAction, 34);
            addResolveItem(ticket, resolveAction, 35);
            addResolveItem(ticket, resolveAction, 43);
            addResolveItem(ticket, resolveAction, 44);
        }

        addReopenItem(ticket, reopenAction, 27);
        addReopenItem(ticket, reopenAction, 28);
        addReopenItem(ticket, reopenAction, 36);
        addReopenItem(ticket, reopenAction, 37);

        if(permission.has(player, options.manageTicketsConfiguration.getPermissionReject())) {
            addRejectItem(ticket, rejectAction, 30);
            addRejectItem(ticket, rejectAction, 31);
            addRejectItem(ticket, rejectAction, 32);
            addRejectItem(ticket, rejectAction, 39);
            addRejectItem(ticket, rejectAction, 40);
            addRejectItem(ticket, rejectAction, 41);
        }
        if(permission.has(player, options.manageTicketsConfiguration.getPermissionDelete())) {
            addDeleteItem(ticket, deleteAction, 8);
        }

        player.closeInventory();
        player.openInventory(getInventory());
        sessionManager.get(player.getUniqueId()).setCurrentGui(this);
    }

    private void addResolveItem(Ticket ticket, IAction action, int slot) {
        ItemStack item = StaffPlus.get().versionProtocol.addNbtString(
            Items.editor(Items.createGreenColoredGlass("Resolve ticket", "Click to mark this ticket as resolved"))
                .setAmount(1)
                .build(), String.valueOf(ticket.getId()));
        setItem(slot, item, action);
    }

    private void addRejectItem(Ticket ticket, IAction action, int slot) {
        ItemStack item = StaffPlus.get().versionProtocol.addNbtString(
            Items.editor(Items.createRedColoredGlass("Reject ticket", "Click to mark this ticket as rejected"))
                .setAmount(1)
                .build(), String.valueOf(ticket.getId()));
        setItem(slot, item, action);
    }

    private void addReopenItem(Ticket ticket, IAction action, int slot) {
        ItemStack item = StaffPlus.get().versionProtocol.addNbtString(
            Items.editor(Items.createGrayColoredGlass("Unassign", "Click to unassign yourself from this ticket"))
                .setAmount(1)
                .build(), String.valueOf(ticket.getId()));
        setItem(slot, item, action);
    }

    private void addDeleteItem(Ticket ticket, IAction action, int slot) {
        ItemStack itemstack = Items.builder()
            .setMaterial(Material.REDSTONE_BLOCK)
            .setName("Delete")
            .addLore("Click to delete this ticket")
            .build();

        ItemStack item = StaffPlus.get().versionProtocol.addNbtString(
            Items.editor(itemstack)
                .setAmount(1)
                .build(), String.valueOf(ticket.getId()));
        setItem(slot, item, action);
    }
}