package net.shortninja.staffplus.staff.ticketing.database;

import net.shortninja.staffplus.staff.ticketing.Ticket;
import net.shortninja.staffplus.util.database.migrations.sqlite.SqlLiteConnection;

import java.sql.*;

public class SqliteTicketRepository extends AbstractSqlTicketRepository {

    @Override
    protected Connection getConnection() throws SQLException {
        return SqlLiteConnection.connect();
    }

    @Override
    public int addTicket(Ticket ticket) {
        try (Connection connection = getConnection();
             PreparedStatement insert = connection.prepareStatement("INSERT INTO sp_tickets(description, creator_uuid, creator_name, status, timestamp, deleted) " +
                 "VALUES(?, ?, ?, ?, ?, ?);")) {
            connection.setAutoCommit(false);
            insert.setString(1, ticket.getDescription());
            insert.setString(2, ticket.getCreatorUuid().toString());
            insert.setString(3, ticket.getCreatorName());
            insert.setString(4, ticket.getTicketStatus().toString());
            insert.setLong(5, ticket.getTimestamp().toInstant().toEpochMilli());
            insert.setBoolean(6, false);
            insert.executeUpdate();

            Statement statement = connection.createStatement();
            ResultSet generatedKeys = statement.executeQuery("SELECT last_insert_rowid()");
            int generatedKey = -1;
            if (generatedKeys.next()) {
                generatedKey = generatedKeys.getInt(1);
            }
            connection.commit(); // Commits transaction.

            return generatedKey;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
