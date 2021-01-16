package net.shortninja.staffplus.staff.ticketing.database;

import net.shortninja.staffplus.event.tickets.TicketStatus;
import net.shortninja.staffplus.staff.ticketing.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractSqlTicketRepository implements TicketRepository {

    protected abstract Connection getConnection() throws SQLException;

    @Override
    public List<Ticket> getUnresolvedTickets(int offset, int amount) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM sp_tickets WHERE status = ? AND deleted=?  ORDER BY timestamp DESC LIMIT ?,?")) {
            ps.setString(1, TicketStatus.OPEN.toString());
            ps.setBoolean(2, false);
            ps.setInt(3, offset);
            ps.setInt(4, amount);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(buildTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tickets;
    }

    @Override
    public List<Ticket> getClosedTickets(int offset, int amount) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM sp_tickets WHERE status IN (?,?,?) AND deleted=?  ORDER BY timestamp DESC LIMIT ?,?")) {
            ps.setString(1, TicketStatus.REJECTED.toString());
            ps.setString(2, TicketStatus.RESOLVED.toString());
            ps.setString(3, TicketStatus.EXPIRED.toString());
            ps.setBoolean(4, false);
            ps.setInt(5, offset);
            ps.setInt(6, amount);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(buildTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tickets;
    }

    @Override
    public Optional<Ticket> findOpenTicket(int ticketId) {
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM sp_tickets WHERE id = ? AND status = ? AND deleted=?")) {
            ps.setInt(1, ticketId);
            ps.setString(2, TicketStatus.OPEN.toString());
            ps.setBoolean(3, false);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = rs.next();
                if (first) {
                    return Optional.of(buildTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Ticket> findTicket(int ticketId) {
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM sp_tickets WHERE id = ? AND deleted=?")) {
            ps.setInt(1, ticketId);
            ps.setBoolean(2, false);
            try (ResultSet rs = ps.executeQuery()) {
                boolean first = rs.next();
                if (first) {
                    return Optional.of(buildTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void updateTicket(Ticket ticket) {
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("UPDATE sp_tickets set staff_name=?, staff_uuid=?, status=?, close_reason=? WHERE id=? AND deleted=?")) {
            insert.setString(1, ticket.getStaffName());
            insert.setString(2, ticket.getStaffUuid() != null ? ticket.getStaffUuid().toString() : null);
            insert.setString(3, ticket.getTicketStatus().toString());
            insert.setString(4, ticket.getCloseReason());
            insert.setInt(5, ticket.getId());
            insert.setBoolean(6, false);
            insert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void markTicketDeleted(Ticket ticket) {
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("UPDATE sp_tickets set deleted=? WHERE id=?")) {
            insert.setBoolean(1, true);
            insert.setInt(2, ticket.getId());
            insert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Ticket> getAssignedTickets(UUID staffUuid, int offset, int amount) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM sp_tickets WHERE staff_uuid = ? AND status = ? AND deleted=? ORDER BY timestamp DESC LIMIT ?,?")) {
            ps.setString(1, staffUuid.toString());
            ps.setString(2, TicketStatus.IN_PROGRESS.toString());
            ps.setBoolean(3, false);
            ps.setInt(4, offset);
            ps.setInt(5, amount);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(buildTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tickets;
    }

    @Override
    public List<Ticket> getMyTickets(UUID creatorUuid, int offset, int amount) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM sp_tickets WHERE creator_uuid = ? AND deleted=? ORDER BY timestamp DESC LIMIT ?,?")) {
            ps.setString(1, creatorUuid.toString());
            ps.setBoolean(2, false);
            ps.setInt(3, offset);
            ps.setInt(4, amount);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(buildTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tickets;
    }

    @Override
    public List<Ticket> getMyTickets(UUID creatorUuid) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection sql = getConnection();
             PreparedStatement ps = sql.prepareStatement("SELECT * FROM sp_tickets WHERE creator_uuid = ? AND deleted=? ORDER BY timestamp")) {
            ps.setString(1, creatorUuid.toString());
            ps.setBoolean(2, false);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(buildTicket(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tickets;
    }

    private Ticket buildTicket(ResultSet rs) throws SQLException {
        UUID creatorUUID = UUID.fromString(rs.getString("creator_uuid"));
        String creatorName = rs.getString("creator_name");
        UUID staffUUID = rs.getString("staff_uuid") != null ? UUID.fromString(rs.getString("staff_uuid")) : null;

        int id = rs.getInt("ID");
        return new Ticket(id,
            rs.getString("description"),
            creatorName,
            creatorUUID,
            rs.getLong("timestamp"),
            TicketStatus.valueOf(rs.getString("status")),
            rs.getString("staff_name"),
            staffUUID,
            rs.getString("close_reason"));
    }

}
