package net.shortninja.staffplus.unordered;

import net.shortninja.staffplus.event.tickets.TicketStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface ITicket {

    String getDescription();

    String getStaffName();

    String getCreatorName();

    UUID getCreatorUuid();

    void setCreatorName(String newName);

    TicketStatus getTicketStatus();

    ZonedDateTime getTimestamp();

    String getCloseReason();

    UUID getStaffUuid();

    int getId();
}
