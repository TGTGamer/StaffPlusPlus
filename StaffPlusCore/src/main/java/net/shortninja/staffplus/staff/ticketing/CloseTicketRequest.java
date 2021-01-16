package net.shortninja.staffplus.staff.ticketing;

import net.shortninja.staffplus.event.tickets.TicketStatus;

public class CloseTicketRequest {

    private final int ticketId;
    private final TicketStatus status;
    private String closeReason;

    public CloseTicketRequest(int ticketId, TicketStatus status, String closeReason) {
        this.ticketId = ticketId;
        this.status = status;
        this.closeReason = closeReason;
    }

    public int getTicketId() {
        return ticketId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }
}
