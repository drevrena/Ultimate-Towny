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

public class TownSetWarp extends SubCommand {

    public TownSetWarp() {
        super("setwarp", "/town setwarp");
        setAliases(Collections.singletonList("createwarp"));
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
            if (town.getWarp() != null) {
                sender.sendMessage(Messages.WARP_ALREADY_SET.getTranslation());
                return;
            }
            if (townyPlayer.getRole() == Role.MEMBER) {
                sender.sendMessage(Messages.NO_PERMISSION.getTranslation());
                return;
            }
            town.setWarp(player.getLocation());
            sender.sendMessage(Messages.WARP_SET.getTranslation().replace("%player%", player.getName()));
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
