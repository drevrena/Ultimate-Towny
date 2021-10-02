package it.flowzz.ultimatetowny.commands.impl.subcommands;

import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.enums.Role;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class TownInvite extends SubCommand {

    public TownInvite() {
        super("invite", "/town invite <player>");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player player) {
            TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
            Player toInvite = Bukkit.getPlayer(args[0]);
            Town town = plugin.getTownHandler().getTowns().get(townyPlayer.getTownId());
            if (toInvite == null) {
                sender.sendMessage(Messages.PLAYER_NOT_FOUND.getTranslation());
                return;
            }
            if (town == null) {
                sender.sendMessage(Messages.NOT_IN_TOWN.getTranslation());
                return;
            }
            if (townyPlayer.getRole().isLessThan(Role.MODERATOR)) {
                sender.sendMessage(Messages.NOT_MODERATOR.getTranslation());
                return;
            }
            if (town.getPlayers().stream().anyMatch(senderPlayer -> senderPlayer.getUuid().equals(toInvite.getUniqueId()))){
                sender.sendMessage(Messages.PLAYER_ALREADY_IN_TOWN.getTranslation().replace("%player%", toInvite.getName()));
                return;
            }
            town.getInvites().put(toInvite.getUniqueId(), System.currentTimeMillis());
            broadcast(Messages.PLAYER_INVITED.getTranslation()
                            .replace("%player%", toInvite.getName())
                            .replace("%sender%", player.getName()),
                    town.getPlayers());
            toInvite.sendMessage(Component.text(Messages.INVITE_RECEIVED.getTranslation()
                            .replace("%town%", town.getName())
                            .replace("%player%", player.getName()))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/town join " + town.getName()))
            );
        } else {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER.getTranslation());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}
