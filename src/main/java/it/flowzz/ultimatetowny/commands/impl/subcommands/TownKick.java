package it.flowzz.ultimatetowny.commands.impl.subcommands;

import com.google.common.collect.Lists;
import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.enums.Role;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TownKick extends SubCommand {

    public TownKick() {
        super("kick", "/town kick <player>");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player player) {
            Player toKick = Bukkit.getPlayer(args[0]);
            TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
            TownyPlayer townyToKick = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
            Town town = plugin.getTownHandler().getTowns().get(townyPlayer.getTownId());
            if (toKick == null) {
                sender.sendMessage(Messages.PLAYER_NOT_FOUND.getTranslation());
                return;
            }
            if (town == null) {
                sender.sendMessage(Messages.NOT_IN_TOWN.getTranslation());
                return;
            }
            Role senderRole = townyPlayer.getRole();
            if (senderRole.isLessThan(Role.MODERATOR)) {
                sender.sendMessage(Messages.NOT_MODERATOR.getTranslation());
                return;
            }
            if (townyToKick.getUuid().equals(townyPlayer.getUuid())) {
                sender.sendMessage(Messages.CANNOT_KICK_YOURSELF.getTranslation());
                return;
            }
            if (townyToKick.getRole().isLessThan(senderRole)) {
                sender.sendMessage(Messages.CANNOT_KICK_HIGHER_ROLE.getTranslation());
                return;
            }
            if (town.getPlayers().stream().noneMatch(senderPlayer -> senderPlayer.getUuid().equals(toKick.getUniqueId()))) {
                sender.sendMessage(Messages.PLAYER_NOT_IN_TOWN.getTranslation().replace("%player%", toKick.getName()));
                return;
            }
            townyToKick.setTownId(null);
            townyToKick.setPlaytime(0);
            townyToKick.setRole(Role.MEMBER);
            broadcast(Messages.PLAYER_KICKED.getTranslation()
                            .replace("%player%", toKick.getName())
                            .replace("%town%", town.getName())
                            .replace("%sender%", player.getName()),
                    town.getPlayers());
            toKick.sendMessage(Messages.PLAYER_GOT_KICKED.getTranslation()
                    .replace("%town%", town.getName())
                    .replace("%sender%", player.getName()));
        } else {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER.getTranslation());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
            Town town = plugin.getTownHandler().getTowns().get(townyPlayer.getTownId());
            return town.getPlayers().stream().map(TownyPlayer::getName).toList();
        }
        return Lists.newArrayList();
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}
