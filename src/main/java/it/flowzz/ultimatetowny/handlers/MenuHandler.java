package it.flowzz.ultimatetowny.handlers;

import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.menu.*;
import it.flowzz.ultimatetowny.menu.manager.TownyInventoryManager;
import it.flowzz.ultimatetowny.models.Town;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class MenuHandler {

    private final UltimateTownyPlugin plugin;
    private final InventoryManager inventoryManager;
    private final SmartInventory ratingMenu;
    private final SmartInventory warpMenu;
    private final SmartInventory topMenu;

    public MenuHandler(UltimateTownyPlugin plugin) {
        this.plugin = plugin;
        this.inventoryManager = new TownyInventoryManager(plugin);

        FileConfiguration configuration = plugin.getConfig();
        ratingMenu = SmartInventory.builder()
                .id("Rating-Menu")
                .provider(new RatingMenu(plugin))
                .size(configuration.getInt("menus.rating.rows"), 9)
                .title(ChatColor.translateAlternateColorCodes('&', configuration.getString("menus.rating.title", "&7Town-Rating")))
                .manager(inventoryManager)
                .build();
        warpMenu = SmartInventory.builder()
                .id("Warp-Menu")
                .provider(new WarpMenu(plugin))
                .size(configuration.getInt("menus.warp.rows"), 9)
                .title(ChatColor.translateAlternateColorCodes('&', configuration.getString("menus.warp.title", "&7Town-Warp")))
                .manager(inventoryManager)
                .build();
        topMenu = SmartInventory.builder()
                .id("Top-Menu")
                .provider(new TopMenu(plugin))
                .size(configuration.getInt("menus.top.rows"), 9)
                .title(ChatColor.translateAlternateColorCodes('&', configuration.getString("menus.top.title", "&7Town-Top")))
                .manager(inventoryManager)
                .build();

    }

    public SmartInventory getTownMenu(Town town) {
        return SmartInventory.builder()
                .id("Town-Menu")
                .provider(new TownMenu(plugin, town))
                .size(plugin.getConfig().getInt("menus.town.rows"), 9)
                .title(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("menus.town.title", "&7Town-Menu")))
                .manager(inventoryManager)
                .build();
    }

    public SmartInventory getUpgradeMenu(UltimateTownyPlugin plugin, Town town) {
        return SmartInventory.builder()
                .id("Town-Upgrade")
                .provider(new UpgradeMenu(plugin, town))
                .size(plugin.getConfig().getInt("menus.upgrade.rows"), 9)
                .title(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("menus.upgrade.title", "&7Town-Upgrade")))
                .manager(inventoryManager)
                .build();
    }

    public SmartInventory getStarsMenu(UltimateTownyPlugin plugin, Town town) {
        return SmartInventory.builder()
                .id("Rating-Stars")
                .provider(new StarsMenu(plugin, town))
                .size(plugin.getConfig().getInt("menus.stars.rows"), 9)
                .title(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("menus.stars.title", "&7Town-Rating-Stars")))
                .manager(inventoryManager)
                .build();
    }
}
