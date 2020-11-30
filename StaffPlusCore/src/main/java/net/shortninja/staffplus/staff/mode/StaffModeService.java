package net.shortninja.staffplus.staff.mode;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.player.attribute.InventorySerializer;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManager;
import net.shortninja.staffplus.session.bungee.BungeeSessionManager;
import net.shortninja.staffplus.staff.mode.item.ModeItem;
import net.shortninja.staffplus.staff.mode.item.ModuleConfiguration;
import net.shortninja.staffplus.staff.vanish.VanishHandler;
import net.shortninja.staffplus.unordered.VanishType;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.lib.JavaUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class StaffModeService {
    private static final Map<UUID, InventoryVault> staffMembersSavedData = new HashMap<>();

    private final MessageCoordinator message;
    private final Options options;
    private final Messages messages;
    private final SessionManager sessionManager;
    private final VanishHandler vanishHandler;
    private final BungeeSessionManager bungeeSessionManager;

    public final ModeItem[] MODE_ITEMS;

    public StaffModeService(MessageCoordinator message, Options options, Messages messages, SessionManager sessionManager, VanishHandler vanishHandler, BungeeSessionManager bungeeSessionManager) {
        this.message = message;
        this.options = options;
        this.messages = messages;
        this.sessionManager = sessionManager;
        this.vanishHandler = vanishHandler;
        this.bungeeSessionManager = bungeeSessionManager;

        MODE_ITEMS = new ModeItem[]{
            new ModeItem("compass", options.modeCompassItem, options.modeCompassSlot, options.modeCompassEnabled),
            new ModeItem("randomTeleport", options.modeRandomTeleportItem, options.modeRandomTeleportSlot, options.modeRandomTeleportEnabled),
            new ModeItem("vanish", options.modeVanishItem, options.modeVanishSlot, options.modeVanishEnabled),
            new ModeItem("guiHub", options.modeGuiItem, options.modeGuiSlot, options.modeGuiEnabled),
            new ModeItem("counter", options.modeCounterItem, options.modeCounterSlot, options.modeCounterEnabled),
            new ModeItem("freeze", options.modeFreezeItem, options.modeFreezeSlot, options.modeFreezeEnabled),
            new ModeItem("cps", options.modeCpsItem, options.modeCpsSlot, options.modeCpsEnabled),
            new ModeItem("examine", options.modeExamineItem, options.modeExamineSlot, options.modeExamineEnabled),
            new ModeItem("follow", options.modeFollowItem, options.modeFollowSlot, options.modeFollowEnabled),
        };
    }

    public List<SppPlayer> getModeUsers() {
        return sessionManager.getAll().stream()
            .filter(PlayerSession::isInStaffMode)
            .map(PlayerSession::getSspPlayer).collect(Collectors.toList());
    }

    public void addMode(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerSession session = sessionManager.get(uuid);
        if (session.isInStaffMode()) {
            return;
        }

        session.setStaffMode(true);
        bungeeSessionManager.sendSynchronizationRequest(player, session);

        InventoryVault modeData = new InventoryVault(uuid, getContents(player), player.getInventory().getArmorContents(), player.getInventory().getExtraContents(),
            player.getLocation(), player.getExp(), player.getAllowFlight(), player.getGameMode(), session.getVanishType());
        staffMembersSavedData.put(uuid, modeData);

        JavaUtils.clearInventory(player);
        setPassive(player, session);
        message.send(player, messages.modeStatus.replace("%status%", messages.enabled), messages.prefixGeneral);
    }

    public void removeMode(Player player) {
        PlayerSession session = sessionManager.get(player.getUniqueId());
        if (!session.isInStaffMode()) {
            return;
        }

        session.setStaffMode(false);
        bungeeSessionManager.sendSynchronizationRequest(player, session);

        unsetPassive(player);
        staffMembersSavedData.remove(player.getUniqueId());
        message.send(player, messages.modeStatus.replace("%status%", messages.disabled), messages.prefixGeneral);
    }

    private void setPassive(Player player, PlayerSession session) {
        if (options.modeFlight && !options.modeCreative) {
            player.setAllowFlight(true);
        } else if (options.modeCreative) {
            player.setGameMode(GameMode.CREATIVE);
        }

        runModeCommands(player, true);
        vanishHandler.addVanish(player, options.modeVanish);

        for (ModeItem modeItem : MODE_ITEMS) {
            if (!modeItem.isEnabled()) {
                continue;
            }

            if (modeItem.getIdentifier().equals("vanish")) {
                modeItem.setItem(session.getVanishType() == options.modeVanish ? options.modeVanishItem : options.modeVanishItemOff);
            }

            player.getInventory().setItem(modeItem.getSlot(), StaffPlus.get().versionProtocol.addNbtString(modeItem.getItem(), modeItem.getIdentifier()));
        }

        for (ModuleConfiguration moduleConfiguration : options.moduleConfigurations.values()) {
            player.getInventory().setItem(moduleConfiguration.getSlot(), StaffPlus.get().versionProtocol.addNbtString(moduleConfiguration.getItem(), moduleConfiguration.getIdentifier()));
        }
    }

    private void unsetPassive(Player player) {
        UUID uuid = player.getUniqueId();
        InventoryVault modeData = staffMembersSavedData.get(uuid);
        InventorySerializer saver = new InventorySerializer(player.getUniqueId());

        if (options.modeOriginalLocation) {
            player.teleport(modeData.getPreviousLocation().setDirection(player.getLocation().getDirection()));
            message.send(player, messages.modeOriginalLocation, messages.prefixGeneral);
        }

        runModeCommands(player, false);
        JavaUtils.clearInventory(player);
        getItems(player, saver);
        player.setExp(saver.getXp());
        player.getInventory().setArmorContents(saver.getArmor());
        player.getInventory().setExtraContents(saver.getOffHand());

        saver.deleteFile();
        player.updateInventory();
        player.setAllowFlight(modeData.hasFlight());
        player.setGameMode(modeData.getGameMode());

        if (modeData.getVanishType() == VanishType.NONE) {
            vanishHandler.removeVanish(player);
        } else vanishHandler.addVanish(player, modeData.getVanishType());
    }

    private void runModeCommands(Player player, boolean isEnabled) {
        for (String command : isEnabled ? options.modeEnableCommands : options.modeDisableCommands) {
            if (command.isEmpty()) {
                continue;
            }

            CommandSender target = (command.trim().startsWith("%player%")) ? player : Bukkit.getConsoleSender();
            command = (command.trim().startsWith("%player%)")) ? command.replaceFirst("%player%", "").trim() : command;
            Bukkit.dispatchCommand(target, command.replace("%player%", player.getName()));
        }
    }

    public static ItemStack[] getContents(Player p) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i <= 35; i++) {
            itemStacks.add(p.getInventory().getItem(i));
        }
        return itemStacks.toArray(new ItemStack[]{});
    }

    private void getItems(Player p, InventorySerializer saver) {
        ItemStack[] contents = saver.getContents();
        for (int i = 0; i < contents.length; i++) {
            p.getInventory().setItem(i, contents[i]);
        }


    }

}