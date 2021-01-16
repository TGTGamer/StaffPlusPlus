package net.shortninja.staffplus.staff.ticketing.gui;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.staff.ticketing.Ticket;
import net.shortninja.staffplus.util.lib.hex.Items;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static net.shortninja.staffplus.util.lib.JavaUtils.formatLines;

public class TicketItemBuilder {

    public static ItemStack build(Ticket ticket) {
        List<String> lore = new ArrayList<String>();

        lore.add("&bStatus: " + ticket.getTicketStatus());
        lore.add("&bTimeStamp: " + ticket.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm")));
        lore.add("&bCreator: " + ticket.getCreatorName());

        lore.add("&bDescription:");
        for (String line : formatLines(ticket.getDescription(), 30)) {
            lore.add("  &b" + line);
        }

        if (StringUtils.isNotEmpty(ticket.getCloseReason())) {
            lore.add("&bClose reason:");
            for (String line : formatLines(ticket.getCloseReason(), 30)) {
                lore.add("  &b" + line);
            }
        }

        ItemStack item = Items.editor(Items.createSkull(ticket.getCreatorName())).setAmount(1)
            .setName("&bTicket by: " + ticket.getCreatorName())
            .setLore(lore)
            .build();

        return StaffPlus.get().versionProtocol.addNbtString(item, String.valueOf(ticket.getId()));
    }


}
