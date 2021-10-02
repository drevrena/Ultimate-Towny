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

public class TownPromote extends SubCommand {

    public TownPromote() {
        super("promote", "/town promote <player>");
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
            if (townyPlayer.getRole() != Role.LEADER) {
                sender.sendMessage(Messages.NOT_LEADER.getTranslation());
                return;
            }
            if (target.getRole().isGreaterThan(Role.COLEADER)) {
                sender.sendMessage(Messages.CANNOT_PROMOTE.getTranslation());
                return;
            }
            target.setRole(target.getRole() == Role.MODERATOR ? Role.COLEADER : Role.MODERATOR);
            broadcast(Messages.PLAYER_PROMOTED.getTranslation().replace("%player%", target.getName()), town.getPlayers());
        } else {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER.getTranslation());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
            Town town = plugin.getTownHandler().getTowns().get(townyPlayer.getTownId());
            return town.getModerators().stream().map(TownyPlayer::getName).toList();
        }
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}
