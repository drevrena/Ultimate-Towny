package it.flowzz.ultimatetowny.commands.impl.subcommands;

import com.google.common.collect.Lists;
import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.enums.Role;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TownWithdraw extends SubCommand {

    public TownWithdraw() {
        super("withdraw", "/town withdraw <money|coin> <amount>");
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
            int amount = Math.abs(getAmount(args[1]));
            if (amount == 0) {
                sender.sendMessage(Messages.INVALID_AMOUNT.getTranslation());
                return;
            }
            switch (args[0]) {
                case "money" -> {
                    double balance = town.getMoney();
                    if (balance - amount < 0) {
                        sender.sendMessage(Messages.NOT_ENOUGH_MONEY.getTranslation());
                        return;
                    }
                    plugin.getEconomy().depositPlayer(player, amount);
                    town.setMoney(town.getMoney() - amount);
                    broadcast(Messages.MONEY_WITHDRAW.getTranslation()
                            .replace("%player%", player.getName())
                            .replace("%amount%", String.valueOf(amount)), town.getPlayers());
                }
                case "coin" -> {
                    double coins = town.getCoins();
                    if (coins - amount < 0) {
                        sender.sendMessage(Messages.NOT_ENOUGH_COINS.getTranslation());
                        return;
                    }
                    plugin.getCoinHandler().deposit(player, amount);
                    town.setCoins(town.getCoins() - amount);
                    broadcast(Messages.COINS_WITHDRAW.getTranslation()
                            .replace("%player%", player.getName())
                            .replace("%amount%", String.valueOf(amount)), town.getPlayers());
                }
                default -> sender.sendMessage(Messages.WRONG_SYNTAX.getTranslation().replace("%syntax%", getCommandSyntax()));
            }
        } else {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER.getTranslation());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> suggestion = Lists.newArrayList();
        if (args.length == 1) suggestion.addAll(Arrays.asList("money", "coin"));
        else suggestion.add("<amount>");
        return suggestion;
    }

    public int getAmount(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getMinArguments() {
        return 2;
    }
}
