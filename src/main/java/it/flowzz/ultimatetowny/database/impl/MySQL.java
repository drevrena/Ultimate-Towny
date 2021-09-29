package it.flowzz.ultimatetowny.database.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.database.IDatabase;
import it.flowzz.ultimatetowny.database.credentials.Credentials;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import it.flowzz.ultimatetowny.models.Upgrade;
import it.flowzz.ultimatetowny.utils.LocationSerializer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

@RequiredArgsConstructor
public class MySQL implements IDatabase {

    private final UltimateTownyPlugin plugin;
    private HikariDataSource dataSource;

    @Override
    public void connect(Credentials credentials) {
        Preconditions.checkNotNull(credentials, "Credentials cannot be null.");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + credentials.getHostname() + ":" + credentials.getPort() + "/" + credentials.getDatabase());
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setPoolName("TownyX-Pool");
        config.setUsername(credentials.getUsername());
        config.setPassword(credentials.getPassword());
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cachePrepStmts", "true");
        dataSource = new HikariDataSource(config);
        createTable();
        //Start Auto-Save Task every 5 min
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::save, 300L * 20, 300L * 20);
    }

    private void createTable() {
        try (Connection connection = dataSource.getConnection()) {
            //TownsTable
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS townyx_towns (name VARCHAR(16), warp TEXT, money DOUBLE, coins DOUBLE, PRIMARY KEY (name))");
            //PlayersTable
            connection.createStatement().execute("""
                    CREATE TABLE IF NOT EXISTS townyx_players (uuid VARCHAR(36), town_id VARCHAR(16), name VARCHAR(16), role VARCHAR(16), playtime BIGINT,\s
                    INDEX townyx_players_ind (town_Id), PRIMARY KEY (uuid))""");
            //UpgradeTable
            connection.createStatement().execute("""
                    CREATE TABLE IF NOT EXISTS townyx_upgrades (town_id VARCHAR(16), upgrade VARCHAR(16), level INT,\s
                    INDEX townyx_players_ind (town_Id),
                    FOREIGN KEY (town_id) REFERENCES townyx_towns(name)
                    ON DELETE CASCADE, PRIMARY KEY (town_id, upgrade))""");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Override
    public void load() {
        long start = System.currentTimeMillis();
        int count = 0;
        try (Connection connection = dataSource.getConnection()) {
            //Load towns and its players/upgrades
            ResultSet townResult = connection.createStatement().executeQuery("SELECT * FROM townyx_towns");
            PreparedStatement playerStatement = connection.prepareStatement("SELECT uuid,town_id,name,role,playtime FROM townyx_players WHERE town_id=?");
            PreparedStatement upgradeStatement = connection.prepareStatement("SELECT upgrade,level FROM townyx_upgrades WHERE town_id=?");
            while (townResult.next()) {
                String townId = townResult.getString("name");
                Set<TownyPlayer> players = Sets.newHashSet();
                Set<Upgrade> upgrades = Sets.newHashSet();
                //Load Town Players
                playerStatement.setString(1, townId);
                ResultSet playersResult = playerStatement.executeQuery();
                while (playersResult.next()) {
                    players.add(new TownyPlayer(playersResult));
                }
                //Load Town upgrades
                upgradeStatement.setString(1, townId);
                ResultSet upgradesResult = upgradeStatement.executeQuery();
                while (upgradesResult.next()) {
                    upgrades.add(new Upgrade(upgradesResult));
                }
                //Load Town Information
                plugin.getTownHandler().getTowns().put(townId, new Town(townResult, townId, players, upgrades));
                //Map players to TownyPlayers
                players.forEach(townyPlayer -> plugin.getTownHandler().getPlayers().put(townyPlayer.getUuid(), townyPlayer));
                count++;
            }
            //Load Townless players
            ResultSet playersResult = connection.createStatement().executeQuery("SELECT * FROM townyx_players WHERE town_id IS NULL");
            while (playersResult.next()) {
                plugin.getTownHandler().getPlayers().put(UUID.fromString(playersResult.getString("uuid")), new TownyPlayer(playersResult));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        plugin.getLogger().log(Level.INFO, String.format("Loaded %d towns in %d ms", count, (System.currentTimeMillis() - start)));
    }

    @Override
    public void save() {
        long start = System.currentTimeMillis();
        int count = 0;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement townStatement = connection.prepareStatement("INSERT INTO townyx_towns (name, warp, money, coins) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE warp=VALUES(warp), money=VALUES(money), coins=VALUES(coins)");
            PreparedStatement playersStatement = connection.prepareStatement("INSERT INTO townyx_players (uuid, town_id, name, role, playtime) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE name=VALUES(name), town_id=VALUES(town_id), role=VALUES(role), playtime=VALUES(playtime)");
            PreparedStatement upgradesStatement = connection.prepareStatement("INSERT INTO townyx_upgrades (town_id, upgrade, level) VALUES (?,?,?) ON DUPLICATE KEY UPDATE level=VALUES(level)");

            for (Town town : plugin.getTownHandler().getTowns().values()) {
                townStatement.setString(1, town.getName());
                townStatement.setString(2, LocationSerializer.serialize(town.getWarp()));
                townStatement.setDouble(3, town.getMoney());
                townStatement.setDouble(4, town.getCoins());
                townStatement.addBatch();
                //Save Town upgrades
                for (Upgrade upgrade : town.getUpgrades()) {
                    upgradesStatement.setString(1, town.getName());
                    upgradesStatement.setString(2, upgrade.getType().name());
                    upgradesStatement.setInt(3, upgrade.getLevel());
                    upgradesStatement.addBatch();
                }
                count++;
            }
            //Save Town players
            for (TownyPlayer townyPlayer : plugin.getTownHandler().getPlayers().values()) {
                playersStatement.setString(1, townyPlayer.getUuid().toString());
                playersStatement.setString(2, townyPlayer.getTownId());
                playersStatement.setString(3, townyPlayer.getName());
                playersStatement.setString(4, townyPlayer.getRole().name());
                playersStatement.setLong(5, townyPlayer.getPlaytime());
                playersStatement.addBatch();
            }
            townStatement.executeBatch();
            playersStatement.executeBatch();
            upgradesStatement.executeBatch();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        plugin.getLogger().log(Level.INFO, String.format("Saved %d towns in %d ms", count, (System.currentTimeMillis() - start)));
    }

    @Override
    public void deleteTown(String townId) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement townStatement = connection.prepareStatement("DELETE FROM townyx_towns WHERE name=?");
            townStatement.setString(1, townId);
            townStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
