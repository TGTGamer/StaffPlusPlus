package net.shortninja.staffplus.staff.ticketing.cmd;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.server.command.AbstractCmd;
import net.shortninja.staffplus.server.command.PlayerRetrievalStrategy;
import net.shortninja.staffplus.staff.ticketing.TicketService;
import net.shortninja.staffplus.util.lib.JavaUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CreateTicketCmd extends AbstractCmd {
    private final TicketService ticketService = IocContainer.getTicketService();

    public CreateTicketCmd(String name) {
        super(name, IocContainer.getOptions().ticketingConfiguration.getCreateTicketPermission());
    }

    @Override
    protected boolean executeCmd(CommandSender sender, String alias, String[] args, SppPlayer player) {
        String reason = JavaUtils.compileWords(args, 0);
        ticketService.createTicket(sender, reason);
        return true;
    }

    @Override
    protected int getMinimumArguments(CommandSender sender, String[] args) {
        return 1;
    }

    @Override
    protected boolean isAuthenticationRequired() {
        return false;
    }

    @Override
    protected PlayerRetrievalStrategy getPlayerRetrievalStrategy() {
        return PlayerRetrievalStrategy.NONE;
    }

    @Override
    protected Optional<String> getPlayerName(CommandSender sender, String[] args) {
        return Optional.empty();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}