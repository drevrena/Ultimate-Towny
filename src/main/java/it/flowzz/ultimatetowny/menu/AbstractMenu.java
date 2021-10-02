package it.flowzz.ultimatetowny.menu;

import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public abstract class AbstractMenu implements InventoryProvider {

    protected final UltimateTownyPlugin plugin;

    protected AbstractMenu(UltimateTownyPlugin plugin) {
        this.plugin = plugin;
    }

    protected int getIndex(String string) {
        int index;
        try {
            index = Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            index = -999;
        }
        return index;
    }

    protected SlotPos getSlot(int index) {
        return SlotPos.of((index / 9), (index % 9));
    }

    protected ItemStack setPlayerSkin(ItemStack itemStack, String playerName) {
        if (itemStack.getType() != Material.PLAYER_HEAD)
            return itemStack;

        SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
        itemMeta.setOwningPlayer(Bukkit.getOfflinePlayerIfCached(playerName));
        itemStack.setItemMeta(itemMeta);
        return itemStack;

    }
}
