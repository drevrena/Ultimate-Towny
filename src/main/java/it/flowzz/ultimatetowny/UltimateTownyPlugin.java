package it.flowzz.ultimatetowny;

import it.flowzz.ultimatetowny.commands.impl.TownCommand;
import it.flowzz.ultimatetowny.database.IDatabase;
import it.flowzz.ultimatetowny.database.credentials.Credentials;
import it.flowzz.ultimatetowny.database.impl.MySQL;
import it.flowzz.ultimatetowny.handlers.CoinHandler;
import it.flowzz.ultimatetowny.handlers.MenuHandler;
import it.flowzz.ultimatetowny.handlers.TownHandler;
import it.flowzz.ultimatetowny.hooks.KrakenMobCoinsHandler;
import it.flowzz.ultimatetowny.hooks.SuperMobCoinsHandler;
import it.flowzz.ultimatetowny.listeners.JoinListener;
import it.flowzz.ultimatetowny.listeners.PlayerListener;
import it.flowzz.ultimatetowny.placeholders.TownyExpansion;
import it.flowzz.ultimatetowny.tasks.CoinGenTask;
import it.flowzz.ultimatetowny.tasks.MoneyGenTask;
import it.flowzz.ultimatetowny.tasks.PlayTimeTask;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Getter
public final class UltimateTownyPlugin extends JavaPlugin {

    @Getter
    private static UltimateTownyPlugin instance;

    private IDatabase database;
    @Setter
    private CoinHandler coinHandler;
    private TownHandler townHandler;
    private MenuHandler menuHandler;
    private Economy economy;

    private FileConfiguration lang;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupConfigs();
        setupDatabase();
        setupHandlers();
        setupEconomy();
        registerTasks();
        registerPlaceholder();
        registerCommands();
        registerListeners(
                new JoinListener(this),
                new PlayerListener(this)
        );
    }

    private void setupConfigs() {
        //Load lang file
        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }
        lang = new YamlConfiguration();
        try {
            lang.load(langFile);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    private void setupDatabase() {
        database = new MySQL(this);
        database.connect(Credentials.builder()
                .hostname(getConfig().getString("database.hostname"))
                .database(getConfig().getString("database.database"))
                .username(getConfig().getString("database.username"))
                .password(getConfig().getString("database.password"))
                .port(getConfig().getInt("database.port"))
                .build()
        );
    }

    private void setupHandlers() {
        townHandler = new TownHandler(this);
        menuHandler = new MenuHandler(this);
        if (Bukkit.getPluginManager().getPlugin("SuperMobCoins") != null) {
            coinHandler = new SuperMobCoinsHandler();
        } else if (Bukkit.getPluginManager().getPlugin("KrakenMobcoins") != null) {
            coinHandler = new KrakenMobCoinsHandler();
        } else {
            getLogger().log(Level.WARNING, "Coin hook not found! If you want to use the plugin without coins support you should set all coins-cost to 0!");
        }
        database.load();
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
    }

    private void registerTasks() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new CoinGenTask(this),
                getConfig().getLong("town.upgrades.coin-gen.timer") * 20, getConfig().getLong("town.upgrades.coin-gen.timer") * 20);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new MoneyGenTask(this),
                getConfig().getLong("town.upgrades.money-gen.timer") * 20, getConfig().getLong("town.upgrades.money-gen.timer") * 20);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new PlayTimeTask(this), 20, 20);
    }

    private void registerPlaceholder() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TownyExpansion(this).register();
        }
    }

    private void registerCommands() {
        PluginCommand townCommand = getCommand("town");
        TownCommand town = new TownCommand();
        townCommand.setExecutor(town);
        townCommand.setTabCompleter(town);
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {
        database.save();
        database.disconnect();
        townHandler.shutdown();
        instance = null;
    }
}
