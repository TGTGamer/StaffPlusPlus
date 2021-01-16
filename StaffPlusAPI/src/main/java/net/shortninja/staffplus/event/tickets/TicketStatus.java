package net.shortninja.staffplus.event.tickets;

public enum TicketStatus {
    OPEN,
    RESOLVED,
    EXPIRED,
    IN_PROGRESS,
    REJECTED;

    public boolean isClosed() {
        return this == RESOLVED || this == EXPIRED || this == REJECTED;
    }
}
