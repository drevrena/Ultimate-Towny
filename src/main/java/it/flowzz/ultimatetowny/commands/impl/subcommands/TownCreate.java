package it.flowzz.ultimatetowny.commands.impl.subcommands;

import com.google.common.collect.Sets;
import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.enums.Role;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class TownCreate extends SubCommand {

    public TownCreate() {
        super("create", "/town create <name>");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player player) {
            TownyPlayer townyPlayer = plugin.getTownHandler().getPlayers().get(player.getUniqueId());
            Town town = plugin.getTownHandler().getTowns().get(townyPlayer.getTownId());
            String townName = args[0];
            if (!Pattern.matches("^[a-zA-Z0-9]{4,16}$", townName)) {
                sender.sendMessage(Messages.INVALID_TOWN_NAME.getTranslation());
                return;
            }
            if (plugin.getConfig().getStringList("town.blacklisted-town-names").contains(townName.toLowerCase())){
                sender.sendMessage(Messages.BLACKLISTED_TOWN_NAME.getTranslation());
                return;
            }
            if (town != null) {
                sender.sendMessage(Messages.ALREADY_IN_TOWN.getTranslation());
                return;
            }
            for (String name : plugin.getTownHandler().getTowns().keySet()) {
                if (name.equalsIgnoreCase(townName)) {
                    sender.sendMessage(Messages.TOWN_ALREADY_EXIST.getTranslation());
                    return;
                }
            }
            double cost = plugin.getConfig().getInt("economy.create-tax");
            if (plugin.getConfig().getBoolean("economy.enabled") && plugin.getEconomy().getBalance(player) < cost) {
                sender.sendMessage(Messages.NOT_ENOUGH_MONEY.getTranslation());
                return;
            }
            townyPlayer.setTownId(townName);
            townyPlayer.setRole(Role.LEADER);
            broadcast(Messages.TOWN_CREATED.getTranslation()
                    .replace("%town%", townName)
                    .replace("%player%", player.getName())
            );
            plugin.getEconomy().withdrawPlayer(player, cost);
            plugin.getTownHandler().getTowns().put(townName, new Town(townName, null, 0, 0, Sets.newHashSet(townyPlayer), Sets.newHashSet()));
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
        return 1;
    }
}
