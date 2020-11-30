package net.shortninja.staffplus.session.config;

import net.shortninja.staffplus.common.config.ConfigLoader;
import org.bukkit.configuration.file.FileConfiguration;

public class SessionSynchronizationConfigLoader extends ConfigLoader<SessionSyncConfiguration> {

    @Override
    protected SessionSyncConfiguration load(FileConfiguration config) {
        boolean syncEnabled = config.getBoolean("session-synchronization.enabled");
        return new SessionSyncConfiguration(syncEnabled);
    }
}
