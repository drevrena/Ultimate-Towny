package it.flowzz.ultimatetowny.commands.impl.subcommands;

import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TownJoin extends SubCommand {

    public TownJoin() {
        super("join", "/town join <town>");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player player) {
            TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
            Town town = plugin.getTownHandler().getTowns().get(args[0]);
            if (town == null) {
                sender.sendMessage(Messages.TOWN_NOT_FOUND.getTranslation());
                return;
            }
            if (townyPlayer.getTownId() != null) {
                sender.sendMessage(Messages.ALREADY_IN_TOWN.getTranslation());
                return;
            }
            if (town.getPlayers().size() >= town.getMemberLimit()) {
                sender.sendMessage(Messages.TOWN_MAX_PLAYERS.getTranslation());
                return;
            }
            long inviteTimestamp = town.getInvites().getOrDefault(player.getUniqueId(), 0L);
            if (inviteTimestamp == 0) {
                sender.sendMessage(Messages.TOWN_NOT_INVITED.getTranslation());
                return;
            }
            if (System.currentTimeMillis() - inviteTimestamp > 300 * 1000) {
                sender.sendMessage(Messages.TOWN_INVITE_EXPIRED.getTranslation());
                return;
            }
            town.getPlayers().add(townyPlayer);
            town.getInvites().remove(player.getUniqueId());
            townyPlayer.setTownId(town.getName());
            broadcast(Messages.PLAYER_JOINED.getTranslation().replace("%player%", player.getName()), town.getPlayers());
        } else {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER.getTranslation());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>(plugin.getTownHandler().getTowns().keySet());
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}
