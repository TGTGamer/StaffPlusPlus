package net.shortninja.staffplus.staff.ticketing.config;

public class ManageTicketsConfiguration {

    private String commandManageTicketsGui;
    private String permissionView;
    private String permissionDelete;
    private String permissionAccept;
    private String permissionResolve;
    private String permissionReject;


    public ManageTicketsConfiguration(String commandManageTicketsGui, String permissionView, String permissionDelete, String permissionAccept, String permissionResolve, String permissionReject) {
        this.commandManageTicketsGui = commandManageTicketsGui;
        this.permissionView = permissionView;
        this.permissionDelete = permissionDelete;
        this.permissionAccept = permissionAccept;
        this.permissionResolve = permissionResolve;
        this.permissionReject = permissionReject;
    }

    public String getCommandManageTicketsGui() {
        return commandManageTicketsGui;
    }

    public String getPermissionView() {
        return permissionView;
    }

    public String getPermissionDelete() {
        return permissionDelete;
    }

    public String getPermissionAccept() {
        return permissionAccept;
    }

    public String getPermissionResolve() {
        return permissionResolve;
    }

    public String getPermissionReject() {
        return permissionReject;
    }
}
