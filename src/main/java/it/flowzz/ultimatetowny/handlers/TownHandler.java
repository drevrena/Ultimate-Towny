package it.flowzz.ultimatetowny.handlers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;

@Getter
public class TownHandler {

    private final Map<String, Town> towns = Maps.newHashMap();
    private final Map<UUID, TownyPlayer> players = Maps.newHashMap();

    private final List<Town> topTowns = Collections.synchronizedList(Lists.newArrayList());
    private final Map<Town, List<TownyPlayer>> topPlaytime = Collections.synchronizedMap(Maps.newHashMap());

    public TownHandler(UltimateTownyPlugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::calculateTop, 20, 6000);
    }

    private void calculateTop() {
        topTowns.clear();
        topPlaytime.clear();
        topTowns.addAll(towns.values().stream().sorted().toList());
        towns.values().forEach(town -> topPlaytime.put(town, town.getPlayers().stream().sorted().toList()));
    }

    public Town getTownByName(String townName) {
        return towns.entrySet().stream()
                .filter(townEntry -> townEntry.getKey().equalsIgnoreCase(townName))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public Town getTownByPlayer(String playerName) {
        return towns.values()
                .stream()
                .filter(town -> town.getPlayers()
                        .stream()
                        .anyMatch(townyPlayer -> townyPlayer.getName().equalsIgnoreCase(playerName)))
                .findFirst()
                .orElse(null);
    }

    public void shutdown() {
        towns.clear();
        players.clear();
    }
}
