package it.flowzz.ultimatetowny.commands.impl.subcommands;

import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TownRate extends SubCommand {

    public TownRate() {
        super("rate", "/town rate <town>");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player player) {
            Town townByPlayer = plugin.getTownHandler().getTownByPlayer(args[0]);
            Town townByName = plugin.getTownHandler().getTownByName(args[0]);
            if (townByPlayer == null && townByName == null) {
                sender.sendMessage(Messages.TOWN_NOT_FOUND.getTranslation());
                return;
            }
            Town town = townByName == null ? townByPlayer : townByName;
            plugin.getMenuHandler().getStarsMenu(plugin, town).open(player);
        } else {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER.getTranslation());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>(plugin.getTownHandler().getTowns().keySet());
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}
