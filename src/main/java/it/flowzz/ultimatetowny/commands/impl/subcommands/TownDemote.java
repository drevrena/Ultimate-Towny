package it.flowzz.ultimatetowny.commands.impl.subcommands;

import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.enums.Role;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class TownDemote extends SubCommand {

    public TownDemote() {
        super("demote", "/town demote <player>");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player player) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (offlinePlayer == null) {
                sender.sendMessage(Messages.PLAYER_NOT_FOUND.getTranslation());
                return;
            }
            TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
            TownyPlayer target = plugin.getTownHandler().getPlayers().get(offlinePlayer.getUniqueId());
            Town town = plugin.getTownHandler().getTowns().get(townyPlayer.getTownId());
            if (town == null) {
                sender.sendMessage(Messages.NOT_IN_TOWN.getTranslation());
                return;
            }
            if (townyPlayer.getRole().isLessThan(Role.COLEADER)) {
                sender.sendMessage(Messages.NOT_LEADER.getTranslation());
                return;
            }
            if (target.getRole() == Role.LEADER) {
                sender.sendMessage(Messages.CANNOT_DEMOTE_LEADER.getTranslation());
                return;
            }
            if (target.getRole() == Role.MEMBER) {
                sender.sendMessage(Messages.ALREADY_MEMBER.getTranslation());
                return;
            }
            target.setRole(target.getRole() == Role.COLEADER ? Role.MODERATOR : Role.MEMBER);
            broadcast(Messages.PLAYER_DEMOTED.getTranslation()
                            .replace("%player%", target.getName())
                            .replace("%rankt%", target.getRole().getTranslation()),
                    town.getPlayers());
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
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}
