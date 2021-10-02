package it.flowzz.ultimatetowny.commands.impl;

import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.commands.impl.subcommands.*;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TownCommand implements CommandExecutor, TabCompleter {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public TownCommand() {
        subCommands.add(new TownCreate());
        subCommands.add(new TownDisband());
        subCommands.add(new TownRename());
        subCommands.add(new TownShow());
        subCommands.add(new TownPromote());
        subCommands.add(new TownDemote());
        subCommands.add(new TownInvite());
        subCommands.add(new TownKick());
        subCommands.add(new TownJoin());
        subCommands.add(new TownLeave());
        subCommands.add(new TownWithdraw());
        subCommands.add(new TownDeposit());
        subCommands.add(new TownWarp());
        subCommands.add(new TownSetWarp());
        subCommands.add(new TownDeleteWarp());
        subCommands.add(new TownRate());
        subCommands.add(new TownRecruit());
        subCommands.add(new TownUpgrade());
        subCommands.add(new TownTop());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (commandSender instanceof Player player) {
                Town town = UltimateTownyPlugin.getInstance().getTownHandler().getTownByPlayer(player.getName());
                if (town == null) {
                    commandSender.sendMessage(Messages.NOT_IN_TOWN.getTranslation());
                    return false;
                }
                UltimateTownyPlugin.getInstance().getMenuHandler().getTownMenu(town).open(player);
            } else {
                commandSender.sendMessage(Messages.COMMAND_ONLY_PLAYER.getTranslation());
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("help")) {
            commandSender.sendMessage(Messages.COMMAND_HELP_HEADER.getTranslation());
            subCommands.stream()
                    .filter(subCommand -> subCommand.getPermission() == null || commandSender.hasPermission(subCommand.getPermission()))
                    .forEach(subCommand -> commandSender.sendMessage(Messages.COMMAND_HELP.getTranslation()
                            .replace("%syntax%", subCommand.getCommandSyntax())
                    ));
            commandSender.sendMessage(Messages.COMMAND_HELP_FOOTER.getTranslation());
            return true;
        }
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getAliases().contains(args[0].toLowerCase()) || subCommand.getName().equalsIgnoreCase(args[0])) {
                if (subCommand.getPermission() != null && !commandSender.hasPermission(subCommand.getPermission())) {
                    commandSender.sendMessage(Messages.NO_PERMISSION.getTranslation());
                    return true;
                }
                if (args.length - 1 >= subCommand.getMinArguments()) {
                    subCommand.execute(commandSender, label, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    commandSender.sendMessage(Messages.WRONG_SYNTAX.getTranslation().replace("%syntax%", subCommand.getCommandSyntax()));
                }
                return true;
            }
        }
        commandSender.sendMessage(Messages.COMMAND_NOT_FOUND.getTranslation());
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return subCommands.stream()
                    .filter(subCommand -> subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission()))
                    .map(SubCommand::getName)
                    .toList();
        }
        return subCommands.stream()
                .filter(subCommand -> subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission()))
                .filter(subCommand -> args[0].equalsIgnoreCase(subCommand.getName()) || subCommand.getAliases().contains(args[0]))
                .flatMap(subCommand -> subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length))
                        .stream())
                .toList();
    }
}