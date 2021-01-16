package net.shortninja.staffplus.staff.ticketing.gui;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.common.config.GuiItemConfig;
import net.shortninja.staffplus.player.attribute.gui.AbstractGui;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.unordered.IAction;
import net.shortninja.staffplus.util.lib.hex.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

import static org.bukkit.Material.PAPER;

public class ManageTicketsGui extends AbstractGui {

    private final Options options = IocContainer.getOptions();

    private final GuiItemConfig closedTicketsGui;
    private final GuiItemConfig assignedTicketsGui;
    private final GuiItemConfig openTicketsGui;

    public ManageTicketsGui(Player player, String title) {
        super(9, title);

        openTicketsGui = options.ticketingConfiguration.getOpenTicketsGui();
        closedTicketsGui = options.ticketingConfiguration.getClosedTicketsGui();
        assignedTicketsGui = options.ticketingConfiguration.getMyAssignedTicketsGui();

        if (openTicketsGui.isEnabled()) {
            setMenuItem(0, buildGuiItem(PAPER, openTicketsGui), (p) -> new OpenTicketsGui(p, openTicketsGui.getTitle(), 0, () -> new ManageTicketsGui(player, title)));
            setMenuItem(1, buildGuiItem(PAPER, assignedTicketsGui), (p) -> new AssignedTicketsGui(p, assignedTicketsGui.getTitle(), 0, () -> new ManageTicketsGui(player, title)));
            setMenuItem(2, buildGuiItem(PAPER, closedTicketsGui), (p) -> new ClosedTicketsGui(p, closedTicketsGui.getTitle(), 0, () -> new ManageTicketsGui(player, title)));
        }

        PlayerSession playerSession = IocContainer.getSessionManager().get(player.getUniqueId());
        player.closeInventory();
        player.openInventory(getInventory());
        playerSession.setCurrentGui(this);
    }

    private void setMenuItem(int menuSlot, ItemStack menuItem, Consumer<Player> guiFunction) {
        setItem(menuSlot, menuItem, new IAction() {
            @Override
            public void click(Player player, ItemStack item, int slot) {
                guiFunction.accept(player);
            }

            @Override
            public boolean shouldClose(Player player) {
                return false;
            }
        });
    }

    private ItemStack buildGuiItem(Material material, GuiItemConfig config) {
        return Items.builder()
            .setMaterial(material).setAmount(1)
            .setName(config.getItemName())
            .addLore(config.getItemLore())
            .build();
    }
}