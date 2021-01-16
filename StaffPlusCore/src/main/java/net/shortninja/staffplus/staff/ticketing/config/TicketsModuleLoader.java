package net.shortninja.staffplus.staff.ticketing.config;

import net.shortninja.staffplus.common.config.ConfigLoader;
import net.shortninja.staffplus.common.config.GuiItemConfig;
import net.shortninja.staffplus.event.tickets.TicketStatus;
import net.shortninja.staffplus.util.lib.Sounds;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class TicketsModuleLoader extends ConfigLoader<TicketingConfiguration> {

    @Override
    protected TicketingConfiguration load(FileConfiguration config) {
        boolean enabled = config.getBoolean("tickets-module.enabled");
        int cooldown = config.getInt("tickets-module.cooldown");
        boolean notifyTicketOnJoin = config.getBoolean("tickets-module.creator-notifications.notify-on-join");
        boolean closingReasonEnabled = config.getBoolean("tickets-module.closing-reason-enabled", true);
        Sounds sound = stringToSound(sanitize(config.getString("tickets-module.sound", "NONE")));
        String myTicketsPermission = config.getString("permissions.view-my-tickets");
        String myTicketsCmd = config.getString("commands.my-tickets");
        String createTicketCmd = config.getString("commands.create-ticket");
        String createTicketPermission = config.getString("permissions.tickets.create");
        List<TicketStatus> creatorNotifyStatuses = stream(config.getString("tickets-module.creator-notifications.status-change-notifications", "").split(";"))
            .filter(s -> !s.isEmpty())
            .map(TicketStatus::valueOf)
            .collect(Collectors.toList());

        boolean modeGuiTickets = config.getBoolean("staff-mode.gui-module.tickets-gui");
        String modeGuiTicketsTitle = config.getString("staff-mode.gui-module.tickets-title");
        String modeGuiTicketsName = config.getString("staff-mode.gui-module.tickets-name");
        String modeGuiTicketsLore = config.getString("staff-mode.gui-module.tickets-lore");
        String modeGuiMyTicketsTitle = config.getString("staff-mode.gui-module.my-tickets-title");
        String modeGuiMyTicketsLore = config.getString("staff-mode.gui-module.my-tickets-lore");
        String modeGuiClosedTicketsTitle = config.getString("staff-mode.gui-module.closed-tickets-title");
        String modeGuiClosedTicketsLore = config.getString("staff-mode.gui-module.closed-tickets-lore");

        GuiItemConfig openTicketsGui = new GuiItemConfig(modeGuiTickets, modeGuiTicketsTitle, modeGuiTicketsName, modeGuiTicketsLore);
        GuiItemConfig myTicketsGui = new GuiItemConfig(modeGuiTickets, modeGuiMyTicketsTitle, modeGuiMyTicketsTitle, modeGuiMyTicketsLore);
        GuiItemConfig closedTicketsGui = new GuiItemConfig(modeGuiTickets, modeGuiClosedTicketsTitle, modeGuiClosedTicketsTitle, modeGuiClosedTicketsLore);

        return new TicketingConfiguration(enabled,
            createTicketCmd,
            createTicketPermission,
            cooldown,
            sound,
            closingReasonEnabled,
            openTicketsGui,
            myTicketsGui,
            closedTicketsGui,
            myTicketsPermission,
            myTicketsCmd,
            notifyTicketOnJoin,
            creatorNotifyStatuses);
    }
}
