package net.shortninja.staffplus.staff.ticketing.database;

import net.shortninja.staffplus.staff.ticketing.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {

    int addTicket(Ticket ticket);

    List<Ticket> getUnresolvedTickets(int offset, int amount);

    Optional<Ticket> findOpenTicket(int ticketId);

    Optional<Ticket> findTicket(int ticketId);

    void updateTicket(Ticket ticket);

    void markTicketDeleted(Ticket ticket);

    List<Ticket> getAssignedTickets(UUID staffUuid, int offset, int amount);

    List<Ticket> getMyTickets(UUID staffUuid, int offset, int amount);

    List<Ticket> getMyTickets(UUID creatorUuid);

    List<Ticket> getClosedTickets(int offset, int amount);

}
