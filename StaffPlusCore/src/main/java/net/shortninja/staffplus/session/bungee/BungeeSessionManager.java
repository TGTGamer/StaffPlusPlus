package net.shortninja.staffplus.session.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.Constants;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.session.PlayerSession;
import net.shortninja.staffplus.session.SessionLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.Collection;

import static net.shortninja.staffplus.common.Constants.BUNGEE_CORD_CHANNEL;

public class BungeeSessionManager implements PluginMessageListener {

    private static final String SUBCHANNEL = "StaffPlusPlusSessionSync";

    private final Options options;
    private final SessionLoader sessionLoader;

    public BungeeSessionManager(Options options, SessionLoader sessionLoader) {
        this.options = options;
        this.sessionLoader = sessionLoader;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(BUNGEE_CORD_CHANNEL)) {
            return;
        }
        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            if (subchannel.equals(SUBCHANNEL)) {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);

                ByteArrayInputStream bi = new ByteArrayInputStream(msgbytes);
                ObjectInputStream si = new ObjectInputStream(bi);
                PlayerSessionDTO playerSessionDTO = (PlayerSessionDTO) si.readObject();

                PlayerSession playerSession = sessionLoader.loadSession(playerSessionDTO.getPlayerUuid());
                playerSession.setStaffMode(playerSessionDTO.isStaffModeEnabled());
                playerSession.setVanishType(playerSessionDTO.getVanishType());
                sessionLoader.saveSession(playerSession);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSynchronizationRequest(CommandSender sender, PlayerSession playerSession) {
        if (!options.sessionSyncConfiguration.isSyncEnabled()) {
            return;
        }

        Player player = getPlayer(sender);
        if (player != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF(SUBCHANNEL);

            byte[] serializedSession = serialize(toDto(playerSession));
            out.writeShort(serializedSession.length);
            out.write(serializedSession);

            player.sendPluginMessage(StaffPlus.get(), Constants.BUNGEE_CORD_CHANNEL, out.toByteArray());
        }
    }

    private Player getPlayer(CommandSender sender) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            if (onlinePlayers.iterator().hasNext()) {
                player = onlinePlayers.iterator().next();
            }
        }
        return player;
    }

    private byte[] serialize(PlayerSessionDTO playerSessionDTO) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(playerSessionDTO);
            so.flush();
            return bo.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PlayerSessionDTO toDto(PlayerSession playerSession) {
        return new PlayerSessionDTO(playerSession.getUuid(), playerSession.isInStaffMode(), playerSession.getVanishType());
    }
}
