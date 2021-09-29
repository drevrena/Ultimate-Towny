package it.flowzz.ultimatetowny.commands.impl.subcommands;

import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.enums.Role;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TownLeave extends SubCommand {

    public TownLeave() {
        super("leave", "/town leave");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player player) {
            TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
            Town town = plugin.getTownHandler().getTowns().get(townyPlayer.getTownId());
            if (town == null) {
                sender.sendMessage(Messages.NOT_IN_TOWN.getTranslation());
                return;
            }
            if (townyPlayer.getRole() == Role.LEADER) {
                sender.sendMessage(Messages.LEADER_CANNOT_LEAVE.getTranslation());
                return;
            }
            townyPlayer.setTownId(null);
            townyPlayer.setPlaytime(0);
            townyPlayer.setRole(Role.MEMBER);
            town.getPlayers().remove(townyPlayer);
            broadcast(Messages.PLAYER_LEFT.getTranslation().replace("%player%", player.getName()), town.getPlayers());
            sender.sendMessage(Messages.TOWN_LEFT.getTranslation().replace("%town%", town.getName()));
        } else {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER.getTranslation());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}
