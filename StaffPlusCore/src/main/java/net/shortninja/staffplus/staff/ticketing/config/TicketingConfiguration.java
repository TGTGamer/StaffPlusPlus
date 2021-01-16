package net.shortninja.staffplus.staff.ticketing.config;

import net.shortninja.staffplus.common.config.GuiItemConfig;
import net.shortninja.staffplus.event.tickets.TicketStatus;
import net.shortninja.staffplus.util.lib.Sounds;

import java.util.List;

public class TicketingConfiguration {

    private final boolean enabled;
    private final int cooldown;
    private final Sounds sound;
    private final boolean closingReasonEnabled;
    private final GuiItemConfig openTicketsGui;
    private final GuiItemConfig assignedTicketsGui;
    private final GuiItemConfig closedTicketsGui;
    private String myTicketsPermission;
    private String myTicketsCmd;
    private String createTicketCmd;
    private boolean notifyCreatorOnJoin;
    private List<TicketStatus> creatorNotifyStatuses;
    private String createTicketPermission;


    public TicketingConfiguration(boolean enabled,
                                  String createTicketCmd,
                                  String createTicketPermission, int cooldown, Sounds sound,
                                  boolean closingReasonEnabled,
                                  GuiItemConfig openTicketsGui,
                                  GuiItemConfig assignedTicketsGui,
                                  GuiItemConfig closedTicketsGui,
                                  String myTicketsPermission, String myTicketsCmd, boolean notifyCreatorOnJoin,
                                  List<TicketStatus> creatorNotifyStatuses) {
        this.enabled = enabled;
        this.createTicketCmd = createTicketCmd;
        this.createTicketPermission = createTicketPermission;
        this.cooldown = cooldown;
        this.sound = sound;
        this.closingReasonEnabled = closingReasonEnabled;
        this.openTicketsGui = openTicketsGui;
        this.assignedTicketsGui = assignedTicketsGui;
        this.closedTicketsGui = closedTicketsGui;
        this.myTicketsPermission = myTicketsPermission;
        this.myTicketsCmd = myTicketsCmd;
        this.notifyCreatorOnJoin = notifyCreatorOnJoin;
        this.creatorNotifyStatuses = creatorNotifyStatuses;
    }

    public boolean isClosingReasonEnabled() {
        return closingReasonEnabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Sounds getSound() {
        return sound;
    }

    public GuiItemConfig getOpenTicketsGui() {
        return openTicketsGui;
    }

    public GuiItemConfig getMyAssignedTicketsGui() {
        return assignedTicketsGui;
    }

    public GuiItemConfig getClosedTicketsGui() {
        return closedTicketsGui;
    }

    public String getMyTicketsPermission() {
        return myTicketsPermission;
    }

    public String getMyTicketsCmd() {
        return myTicketsCmd;
    }

    public boolean isNotifyCreatorOnJoin() {
        return notifyCreatorOnJoin;
    }

    public List<TicketStatus> getCreatorNotifyStatuses() {
        return creatorNotifyStatuses;
    }

    public String getCreateTicketPermission() {
        return createTicketPermission;
    }

    public String getCreateTicketCmd() {
        return createTicketCmd;
    }
}
