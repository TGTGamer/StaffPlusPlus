package net.shortninja.staffplus.staff.ticketing.config;

import net.shortninja.staffplus.common.config.ConfigLoader;
import org.bukkit.configuration.file.FileConfiguration;

public class ManageTicketsModuleLoader extends ConfigLoader<ManageTicketsConfiguration> {

    @Override
    protected ManageTicketsConfiguration load(FileConfiguration config) {
        String commandManageTicketsGui = config.getString("commands.tickets.manage.gui");
        String permissionManageTicketsView = config.getString("permissions.tickets.manage.view");
        String permissionManageTicketsDelete = config.getString("permissions.tickets.manage.delete");
        String permissionManageTicketsAccept = config.getString("permissions.tickets.manage.accept");
        String permissionManageTicketsResolve = config.getString("permissions.tickets.manage.resolve");
        String permissionManageTicketsReject = config.getString("permissions.tickets.manage.reject");

        return new ManageTicketsConfiguration(
            commandManageTicketsGui,
            permissionManageTicketsView,
            permissionManageTicketsDelete,
            permissionManageTicketsAccept,
            permissionManageTicketsResolve,
            permissionManageTicketsReject
        );
    }
}
