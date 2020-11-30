package net.shortninja.staffplus.staff.alerts;

import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionManager;
import net.shortninja.staffplus.unordered.AlertType;
import net.shortninja.staffplus.unordered.altdetect.IAltDetectResult;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import net.shortninja.staffplus.util.lib.JavaUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class AlertCoordinator {
    private final static Set<Location> notifiedLocations = new HashSet<>();
    private final PermissionHandler permission;
    private final MessageCoordinator message;
    private final Options options;
    private final Messages messages;
    private final SessionManager sessionManager;

    public AlertCoordinator(PermissionHandler permission, MessageCoordinator message, Options options, Messages messages, SessionManager sessionManager) {
        this.permission = permission;
        this.message = message;
        this.options = options;
        this.messages = messages;
        this.sessionManager = sessionManager;
    }

    public boolean hasNotified(Location location) {
        return notifiedLocations.contains(location);
    }

    public int getNotifiedAmount() {
        return notifiedLocations.size();
    }

    public void addNotified(Location location) {
        notifiedLocations.add(location);
    }

    public void clearNotified() {
        notifiedLocations.clear();
    }

    public void onNameChange(String originalName, String newName) {
        if (!options.alertsNameNotify) {
            return;
        }

        for (PlayerSession playerSession : sessionManager.getAll()) {
            Player player = playerSession.getSspPlayer().getPlayer();

            if (playerSession.shouldNotify(AlertType.NAME_CHANGE) && permission.has(player, options.permissionNameChange)) {
                message.send(playerSession.getSspPlayer().getPlayer(), messages.alertsName.replace("%old%", originalName).replace("%new%", newName), messages.prefixGeneral, options.permissionNameChange);
            }
        }
    }

    public void onAltDetect(IAltDetectResult altDetectResult) {
        if (!options.alertsAltDetectEnabled || !options.alertsAltDetectTrustLevels.contains(altDetectResult.getAltDetectTrustLevel())) {
            return;
        }

        for (PlayerSession playerSession : sessionManager.getAll()) {
            Player player = playerSession.getSspPlayer().getPlayer();
            if (playerSession.shouldNotify(AlertType.ALT_DETECT) && permission.has(player, options.permissionAlertsAltDetect)) {
                message.send(player, String.format("&CAlt account check triggered, %s and %s might be the same player. Trust [%s]",
                    altDetectResult.getPlayerCheckedName(),
                    altDetectResult.getPlayerMatchedName(),
                    altDetectResult.getAltDetectTrustLevel()), messages.prefixGeneral);
            }
        }
    }

    public void onMention(PlayerSession user, String mentioner) {
        if (!options.alertsMentionNotify || user == null || !user.getSspPlayer().isOnline()) {
            return;
        }

        if (user.shouldNotify(AlertType.MENTION) && permission.has(user.getSspPlayer().getPlayer(), options.permissionMention)) {
            message.send(user.getSspPlayer().getPlayer(), messages.alertsMention.replace("%target%", mentioner), messages.prefixGeneral, options.permissionMention);
            options.alertsSound.play(user.getSspPlayer().getPlayer());
        }
    }

    public void onXray(String miner, int amount, Material type, int lightLevel) {
        if (!options.alertsXrayEnabled) {
            return;
        }

        for (PlayerSession user : sessionManager.getAll()) {
            Player player = user.getSspPlayer().getPlayer();
            if (user.shouldNotify(AlertType.XRAY) && permission.has(player, options.permissionXray)) {
                message.send(player, messages.alertsXray.replace("%target%", miner).replace("%count%", Integer.toString(amount)).replace("%itemtype%", JavaUtils.formatTypeName(type)).replace("%lightlevel%", Integer.toString(lightLevel)), messages.prefixGeneral, options.permissionXray);
            }
        }
    }
}