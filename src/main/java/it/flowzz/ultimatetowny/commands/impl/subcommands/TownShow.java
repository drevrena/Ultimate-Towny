package it.flowzz.ultimatetowny.commands.impl.subcommands;

import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Placeholder;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TownShow extends SubCommand {

    public TownShow() {
        super("show", "/town show [town]");
        setAliases(Collections.singletonList("info"));
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            if (sender instanceof Player player) {
                TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
                Town town = plugin.getTownHandler().getTowns().get(townyPlayer.getTownId());
                if (town == null) {
                    sender.sendMessage(Messages.NOT_IN_TOWN.getTranslation());
                    return;
                }
                sendMessage(sender, town);
            } else {
                sender.sendMessage(Messages.COMMAND_ONLY_PLAYER.getTranslation());
            }
            return;
        }
        Town townByPlayer = plugin.getTownHandler().getTownByPlayer(args[0]);
        Town townByName = plugin.getTownHandler().getTownByName(args[0]);
        if (townByPlayer == null && townByName == null) {
            sender.sendMessage(Messages.TOWN_NOT_FOUND.getTranslation());
            return;
        }
        sendMessage(sender, townByName == null ? townByPlayer : townByName);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>(plugin.getTownHandler().getTowns().keySet());
    }

    private void sendMessage(CommandSender sender, Town town) {
        sender.sendMessage(
                Component.text(Messages.TOWN_SHOW.getTranslation()
                                .replace("%name%", town.getName())
                                .replace("%money%", String.format("%d", Math.round(town.getMoney())))
                                .replace("%coins%", String.format("%d", Math.round(town.getCoins())))
                                .replace("%rating%", String.format("%.2f", Double.isNaN(town.getAverageRate()) ? 0.0 : town.getAverageRate())))
                        .replaceText(TextReplacementConfig.builder().match("%leader%").replacement(Component.text(town.getLeader().isOnline() ?
                                Messages.ONLINE_COLOR.getTranslation() + town.getLeader().getName() :
                                Messages.OFFLINE_COLOR.getTranslation() + town.getLeader().getName()).hoverEvent(getHover(town.getLeader()))).build())
                        .replaceText(TextReplacementConfig.builder().match("%coleaders%").replacement(formatList(town.getCoLeaders())).build())
                        .replaceText(TextReplacementConfig.builder().match("%moderators%").replacement(formatList(town.getModerators())).build())
                        .replaceText(TextReplacementConfig.builder().match("%members%").replacement(formatList(town.getMembers())).build())
        );
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private Component formatList(List<TownyPlayer> members) {
        Component component = Component.text("");
        for (TownyPlayer member : members) {
            component = component.append(Component.text(member.isOnline() ? Messages.ONLINE_COLOR.getTranslation() + member.getName() : Messages.OFFLINE_COLOR.getTranslation() + member.getName()));
            component = component.hoverEvent(getHover(member).asHoverEvent());
        }
        return component;
    }

    private Component getHover(TownyPlayer townyPlayer) {
        return Component.text(Messages.TOWN_PLAYER_HOVER.getTranslation()
                .replace("%money%", String.format("%d", Math.round(plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(townyPlayer.getUuid())))))
                .replace("%role%", townyPlayer.getRole().getTranslation())
                .replace("%playtime%", DurationFormatUtils.formatDuration(townyPlayer.getPlaytime() * 1000, "d 'Days and' HH:mm:ss")));

    }
}
