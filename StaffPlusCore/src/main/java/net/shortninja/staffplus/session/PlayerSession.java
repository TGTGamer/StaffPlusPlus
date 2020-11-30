package net.shortninja.staffplus.session;

import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.player.attribute.gui.IGui;
import net.shortninja.staffplus.server.chat.ChatAction;
import net.shortninja.staffplus.unordered.AlertType;
import net.shortninja.staffplus.unordered.VanishType;
import org.bukkit.Material;

import java.util.*;

public class PlayerSession {

    private final UUID uuid;
    private final String name;
    private boolean staffMode;
    private Material glassColor;
    private VanishType vanishType = VanishType.NONE;
    private IGui currentGui = null;
    private ChatAction chatAction = null;
    private Map<AlertType, Boolean> alertOptions = new HashMap<>();
    private List<String> playerNotes = new ArrayList<>();
    private SppPlayer player;

    private boolean isChatting = false;
    private boolean isFrozen = false;
    private boolean isProtected = false;

    public PlayerSession(SppPlayer player, Material glassColor, List<String> playerNotes, Map<AlertType, Boolean> alertOptions, boolean staffModeEnabled, VanishType vanishType) {
        this.uuid = player.getId();
        this.name = player.getUsername();
        this.glassColor = glassColor;
        this.playerNotes = playerNotes;
        this.alertOptions = alertOptions;
        this.player = player;
        this.staffMode = staffModeEnabled;
        this.vanishType = vanishType;
    }

    public PlayerSession(SppPlayer player) {
        this.uuid = player.getId();
        this.glassColor = Material.WHITE_STAINED_GLASS_PANE;
        this.name = player.getUsername();
        this.player = player;

        for (AlertType alertType : AlertType.values()) {
            setAlertOption(alertType, true);
        }
    }

    public SppPlayer getSspPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Material getGlassColor() {
        return glassColor;
    }

    public void setGlassColor(Material glassColor) {
        this.glassColor = glassColor;
    }

    public List<String> getPlayerNotes() {
        return playerNotes;
    }

    public VanishType getVanishType() {
        return vanishType;
    }

    /**
     * This method should NOT be used if you want to update the user's vanish
     * type! Use the vanish handler!
     */
    public void setVanishType(VanishType vanishType) {
        this.vanishType = vanishType;
    }

    public Optional<IGui> getCurrentGui() {
        return Optional.ofNullable(currentGui);
    }

    public void setCurrentGui(IGui currentGui) {
        this.currentGui = currentGui;
    }

    public ChatAction getChatAction() {
        return chatAction;
    }

    public void setChatAction(ChatAction chatAction) {
        this.chatAction = chatAction;
    }

    public boolean shouldNotify(AlertType alertType) {
        return alertOptions.get(alertType) != null && alertOptions.get(alertType);
    }

    public boolean inStaffChatMode() {
        return isChatting;
    }

    public void setChatting(boolean isChatting) {
        this.isChatting = isChatting;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public void setAlertOption(AlertType alertType, boolean isEnabled) {
        if (alertOptions.containsKey(alertType)) {
            alertOptions.replace(alertType, isEnabled);
        } else {
            alertOptions.put(alertType, isEnabled);
        }
    }

    public void setStaffMode(boolean staffMode) {
        this.staffMode = staffMode;
    }

    public boolean isInStaffMode() {
        return staffMode;
    }

    public void addPlayerNote(String note) {
        playerNotes.add(note);
    }

    public boolean isVanished() {
        return this.getVanishType() == VanishType.TOTAL;
    }
}