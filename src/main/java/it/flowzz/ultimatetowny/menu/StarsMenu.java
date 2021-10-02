package it.flowzz.ultimatetowny.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.models.Rate;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.utils.ItemStackBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class StarsMenu extends AbstractMenu {

    private final Town town;

    public StarsMenu(UltimateTownyPlugin plugin, Town town) {
        super(plugin);
        this.town = town;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = plugin.getConfig().getConfigurationSection("menus.stars.items");

        for (String slot : itemSection.getKeys(false)) {
            int index = getIndex(slot);
            if (index == -999)
                continue;
            contents.set(getSlot(index), ClickableItem.empty(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + "." + slot)));
        }

        for (int i = 1; i <= 5; i++) {
            int rating = i;
            contents.set(getSlot(itemSection.getInt(i + "-stars" + ".slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + "." + i + "-stars" + ".item"), event -> {
                town.getRatings().add(new Rate(player.getUniqueId(), rating));
                player.closeInventory();
            }));
        }

    }
}
