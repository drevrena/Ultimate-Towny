package it.flowzz.ultimatetowny.commands.impl.subcommands;

import com.google.common.collect.Maps;
import it.flowzz.ultimatetowny.commands.SubCommand;
import it.flowzz.ultimatetowny.enums.Role;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TownRecruit extends SubCommand {

    private HashMap<String, Long> cooldowns;

    public TownRecruit() {
        super("recruit", "/town recruit");
        cooldowns = Maps.newHashMap();
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
            String townName = town.getName();
            if (townyPlayer.getRole().isLessThan(Role.MODERATOR)) {
                sender.sendMessage(Messages.NOT_MODERATOR.getTranslation());
                return;
            }
            long lastRecruit = cooldowns.getOrDefault(townName, 0L);
            if (lastRecruit == 0 || System.currentTimeMillis() - lastRecruit > plugin.getConfig().getLong("towny.recruit-cooldown", 300) * 1000) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
                }
                broadcast(Messages.RECRUIT_MESSAGE.getTranslation()
                        .replace("%town%", townName)
                        .replace("%sender%", player.getName())
                );
                cooldowns.put(townName, System.currentTimeMillis());
                return;
            }
            sender.sendMessage(Messages.RECRUIT_COOLDOWN.getTranslation());
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
        return 0;
    }
}
