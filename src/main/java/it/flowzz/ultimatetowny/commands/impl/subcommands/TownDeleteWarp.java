package it.flowzz.ultimatetowny.commands.impl.subcommands;

import com.google.common.collect.Lists;
import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.enums.Role;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TownDeleteWarp extends SubCommand {

    public TownDeleteWarp() {
        super("delwarp", "/town deletewarp");
        setAliases(Lists.newArrayList("delwarp", "removewarp", "remwarp"));
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
            if (town.getWarp() == null) {
                sender.sendMessage(Messages.WARP_NOT_SET.getTranslation());
                return;
            }
            if (townyPlayer.getRole().isLessThan(Role.MODERATOR)) {
                sender.sendMessage(Messages.NO_PERMISSION.getTranslation());
                return;
            }
            town.setWarp(null);
            sender.sendMessage(Messages.WARP_REMOVED.getTranslation().replace("%player%", player.getName()));
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
