package net.shortninja.staffplus.staff.ticketing;

import net.shortninja.staffplus.event.tickets.TicketStatus;
import net.shortninja.staffplus.unordered.ITicket;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class Ticket implements ITicket {
    private final String description;
    private final ZonedDateTime timestamp;
    private final UUID creatorUuid;
    private String creatorName;
    private String staffName;
    private UUID staffUuid;
    private TicketStatus ticketStatus;
    private int id;
    private String closeReason;

    public Ticket(int id, String description, String creatorName, UUID creatorUuid, long time,
                  TicketStatus ticketStatus,
                  String staffName,
                  UUID staffUuid,
                  String closeReason) {
        this.description = description;
        this.creatorName = creatorName;
        this.creatorUuid = creatorUuid;
        this.id = id;
        this.timestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        this.ticketStatus = ticketStatus;
        this.staffName = staffName;
        this.staffUuid = staffUuid;
        this.closeReason = closeReason;
    }

    public Ticket(String description, String creatorName, UUID creatorUuid, TicketStatus ticketStatus, ZonedDateTime timestamp) {
        this.description = description;
        this.creatorName = creatorName;
        this.creatorUuid = creatorUuid;
        this.ticketStatus = ticketStatus;
        this.timestamp = timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public UUID getCreatorUuid() {
        return creatorUuid;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }


    public UUID getStaffUuid() {
        return staffUuid;
    }

    public void setStaffUuid(UUID staffUuid) {
        this.staffUuid = staffUuid;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }

    public Long getCreationTimestamp() {
        return Timestamp.valueOf(timestamp.toLocalDateTime()).getTime();
    }
}