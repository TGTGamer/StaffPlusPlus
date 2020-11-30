package net.shortninja.staffplus.session;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SessionManager {
    private static Map<UUID, PlayerSession> playerSessions;
    private final SessionLoader sessionLoader;

    public SessionManager(SessionLoader sessionLoader) {
        this.sessionLoader = sessionLoader;
        playerSessions = new HashMap<>();
    }

    public void initialize(Player player) {
        if(!has(player.getUniqueId())) {
            playerSessions.put(player.getUniqueId(), sessionLoader.loadSession(player));
        }
    }

    public Collection<PlayerSession> getAll() {
        return playerSessions.values().stream()
            .filter(p -> p.getSspPlayer().isOnline())
            .collect(Collectors.toList());
    }

    public PlayerSession get(UUID uuid) {
        if(!has(uuid)) {
            PlayerSession playerSession = sessionLoader.loadSession(uuid);
            playerSessions.put(uuid, playerSession);
        }
        return playerSessions.get(uuid);
    }

    public boolean has(UUID uuid) {
        return playerSessions.containsKey(uuid);
    }

    public void unload(UUID uniqueId) {
        if(!has(uniqueId)) {
            return;
        }
        sessionLoader.saveSession(playerSessions.get(uniqueId));
        playerSessions.remove(uniqueId);
    }
}