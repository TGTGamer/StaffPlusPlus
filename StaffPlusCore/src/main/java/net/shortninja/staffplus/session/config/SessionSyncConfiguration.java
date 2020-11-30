package net.shortninja.staffplus.session.config;

public class SessionSyncConfiguration {
    private final boolean syncEnabled;

    public SessionSyncConfiguration(boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }

    public boolean isSyncEnabled() {
        return syncEnabled;
    }
}
