package it.flowzz.ultimatetowny.models;

import com.google.common.collect.Maps;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.enums.Role;
import it.flowzz.ultimatetowny.enums.UpgradeType;
import it.flowzz.ultimatetowny.utils.LocationSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Setter
@Getter
public class Town implements Comparable<Town> {

    private String name;
    private Location warp;
    private double money;
    private double coins;
    private Set<TownyPlayer> players;
    private Set<Upgrade> upgrades;
    private Set<Rate> ratings;

    private final int baseMembers;
    private HashMap<UUID, Long> invites;

    public Town(String name, Location warp, double money, double coins, Set<TownyPlayer> players, Set<Upgrade> upgrades, Set<Rate> ratings) {
        this.name = name;
        this.warp = warp;
        this.money = money;
        this.coins = coins;
        this.players = players;
        this.upgrades = upgrades;
        this.ratings = ratings;
        this.baseMembers = UltimateTownyPlugin.getInstance().getConfig().getInt("town.starting-members");
        this.invites = Maps.newHashMap();
    }

    public Town(ResultSet townResult, String townId, Set<TownyPlayer> players, Set<Upgrade> upgrades, Set<Rate> ratings) throws SQLException {
        this(townId,
                LocationSerializer.deserialize(townResult.getString("warp")),
                townResult.getDouble("money"),
                townResult.getDouble("coins"),
                players,
                upgrades,
                ratings
        );
    }

    public int getMemberLimit() {
        Upgrade membersUpgrade = getUpgrade(UpgradeType.MEMBERS);
        return membersUpgrade == null ? baseMembers : baseMembers + UltimateTownyPlugin.getInstance().getConfig().getInt("town.upgrades.members." + membersUpgrade.getLevel() + ".extra-members");
    }

    public Upgrade getUpgrade(UpgradeType type) {
        return upgrades.stream().filter(upgrade -> upgrade.getType() == type).findFirst().orElse(null);
    }

    public int getUpgradeLevel(UpgradeType type) {
        return upgrades.stream().filter(upgrade -> upgrade.getType() == type).findFirst().map(Upgrade::getLevel).orElse(0);
    }

    public List<TownyPlayer> getMembers() {
        return players.stream().filter(player -> player.getRole() == Role.MEMBER).toList();
    }

    public List<TownyPlayer> getModerators() {
        return players.stream().filter(player -> player.getRole() == Role.MODERATOR).toList();
    }

    public List<TownyPlayer> getCoLeaders() {
        return players.stream().filter(player -> player.getRole() == Role.COLEADER).toList();
    }

    public TownyPlayer getLeader() {
        return players.stream().filter(player -> player.getRole() == Role.LEADER).findFirst().orElse(null);
    }

    public double getAverageRate(){
        return ratings.stream().mapToDouble(Rate::getRating).sum() / ratings.size();
    }

    public double getTotalValue() {
        return money + coins * UltimateTownyPlugin.getInstance().getConfig().getDouble("town.coin-top-value");
    }

    public boolean hasRating(UUID uniqueId) {
        return ratings.stream().anyMatch(rate -> rate.getPlayer().equals(uniqueId));
    }

    public boolean removeRating(UUID uniqueId) {
       return ratings.removeIf(rate -> rate.getPlayer().equals(uniqueId));
    }

    @Override
    public int compareTo(@NotNull Town town) {
        return Double.compare(town.getTotalValue(), getTotalValue());
    }

}
