package net.shortninja.staffplus.util.database.migrations.common;

import net.shortninja.staffplus.util.database.migrations.Migration;

public class V28_DropOldTicketsTableMigration implements Migration {
    @Override
    public String getStatement() {
        return "DROP TABLE IF EXISTS sp_tickets;";
    }

    @Override
    public int getVersion() {
        return 28;
    }
}
