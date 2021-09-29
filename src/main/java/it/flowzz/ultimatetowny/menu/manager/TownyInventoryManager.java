package it.flowzz.ultimatetowny.menu.manager;

import fr.minuskube.inv.InventoryManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TownyInventoryManager extends InventoryManager {

    public TownyInventoryManager(JavaPlugin plugin) {
        super(plugin);
        init();
    }
}
