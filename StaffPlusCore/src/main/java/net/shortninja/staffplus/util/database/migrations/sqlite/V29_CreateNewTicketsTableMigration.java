package net.shortninja.staffplus.util.database.migrations.sqlite;

import net.shortninja.staffplus.util.database.migrations.Migration;

public class V29_CreateNewTicketsTableMigration implements Migration {
    @Override
    public String getStatement() {
        return "CREATE TABLE IF NOT EXISTS sp_tickets (  ID INTEGER PRIMARY KEY,  " +
            "description VARCHAR(255) NOT NULL, " +
            "creator_uuid VARCHAR(36) NOT NULL, " +
            "creator_name VARCHAR(32) NOT NULL, " +
            "staff_uuid VARCHAR(36) NULL, " +
            "staff_name VARCHAR(32) NULL, " +
            "status VARCHAR(16) NOT NULL DEFAULT 'OPEN', " +
            "close_reason text NULL, " +
            "timestamp BIGINT NOT NULL," +
            "deleted boolean not null default false" +
            ")";
    }

    @Override
    public int getVersion() {
        return 29;
    }
}
