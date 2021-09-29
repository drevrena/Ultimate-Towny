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

public class TownRename extends SubCommand {

    public TownRename() {
        super("rename", "/town rename <name>");
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
            if (plugin.getConfig().getStringList("town.blacklisted-town-names").contains(townName.toLowerCase())) {
                sender.sendMessage(Messages.BLACKLISTED_TOWN_NAME.getTranslation());
                return;
            }
            if (town == null) {
                sender.sendMessage(Messages.NOT_IN_TOWN.getTranslation());
                return;
            }
            if (townyPlayer.getRole() != Role.LEADER) {
                sender.sendMessage(Messages.NOT_LEADER.getTranslation());
                return;
            }
            for (String name : plugin.getTownHandler().getTowns().keySet()) {
                if (name.equalsIgnoreCase(townName)) {
                    sender.sendMessage(Messages.TOWN_ALREADY_EXIST.getTranslation());
                    return;
                }
            }
            double cost = plugin.getConfig().getInt("economy.rename-tax");
            if (plugin.getConfig().getBoolean("economy.enabled") && town.getMoney() < cost) {
                sender.sendMessage(Messages.NOT_ENOUGH_TOWN_MONEY.getTranslation());
                return;
            }
            //Make em pay
            town.setMoney(town.getMoney() - cost);
            //Delete old name data
            plugin.getTownHandler().getTowns().remove(town.getName());
            plugin.getTownHandler().getTopPlaytime().remove(town);
            plugin.getTownHandler().getTopTowns().remove(town);
            plugin.getDatabase().deleteTown(town.getName());
            //change the actual name
            String oldName = town.getName();
            town.setName(townName);
            town.getPlayers().forEach(tPlayer -> tPlayer.setTownId(townName));
            //add the new dat to cache
            plugin.getTownHandler().getTowns().put(townName, town);

            broadcast(Messages.TOWN_RENAMED.getTranslation()
                    .replace("%old_town%", oldName)
                    .replace("%new_town%", townName)
                    .replace("%player%", player.getName())
            );
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
