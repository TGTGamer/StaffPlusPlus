package net.shortninja.staffplus.event.tickets;

import net.shortninja.staffplus.unordered.ITicket;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CloseTicketEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ITicket ticket;

    public CloseTicketEvent(ITicket ticket) {
        this.ticket = ticket;
    }

    public ITicket getTicket() {
        return ticket;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
