package net.shortninja.staffplus.staff.ticketing.database;

import net.shortninja.staffplus.staff.ticketing.Ticket;
import net.shortninja.staffplus.util.database.migrations.mysql.MySQLConnection;

import java.sql.*;

public class MysqlTicketRepository extends AbstractSqlTicketRepository {

    @Override
    protected Connection getConnection() throws SQLException {
        return MySQLConnection.getConnection();
    }

    @Override
    public int addTicket(Ticket ticket) {
        try (Connection sql = getConnection();
             PreparedStatement insert = sql.prepareStatement("INSERT INTO sp_tickets(description, creator_uuid, creator_name, status, timestamp) " +
                 "VALUES(?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, ticket.getDescription());
            insert.setString(2, ticket.getCreatorUuid().toString());
            insert.setString(3, ticket.getCreatorName());
            insert.setString(4, ticket.getTicketStatus().toString());
            insert.setLong(5, ticket.getTimestamp().toInstant().toEpochMilli());
            insert.executeUpdate();

            ResultSet generatedKeys = insert.getGeneratedKeys();
            int generatedKey = -1;
            if (generatedKeys.next()) {
                generatedKey = generatedKeys.getInt(1);
            }

            return generatedKey;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
