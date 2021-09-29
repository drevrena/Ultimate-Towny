package it.flowzz.ultimatetowny.models;

import it.flowzz.ultimatetowny.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TownyPlayer implements Comparator<TownyPlayer> {

    private UUID uuid;
    private String townId;
    private String name;
    private Role role;
    private long playtime;

    public TownyPlayer(ResultSet resultSet) throws SQLException {
        this(UUID.fromString(resultSet.getString("uuid")),
                resultSet.getString("town_id"),
                resultSet.getString("name"),
                Role.valueOf(resultSet.getString("role")),
                resultSet.getLong("playtime"));
    }

    public TownyPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.townId = null;
        this.name = player.getName();
        this.role = Role.MEMBER;
        this.playtime = 0;
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public int compare(TownyPlayer townyPlayer1, TownyPlayer townyPlayer2) {
        return Long.compare(townyPlayer2.getPlaytime(), townyPlayer1.getPlaytime());
    }
}
