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

public class TownDisband extends SubCommand {

    public TownDisband() {
        super("disband", "/town disband");
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
            if (townyPlayer.getRole() != Role.LEADER) {
                sender.sendMessage(Messages.NOT_LEADER.getTranslation());
                return;
            }
            for (TownyPlayer townPlayer : town.getPlayers()) {
                townPlayer.setTownId(null);
                townPlayer.setPlaytime(0);
                townPlayer.setRole(Role.MEMBER);
            }
            broadcast(Messages.TOWN_DISBANDED.getTranslation()
                    .replace("%player%", player.getName())
                    .replace("%town%", town.getName())
            );
            plugin.getTownHandler().getTowns().remove(town.getName());
            plugin.getTownHandler().getTopPlaytime().remove(town);
            plugin.getTownHandler().getTopTowns().remove(town);
            plugin.getDatabase().deleteTown(town.getName());

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
