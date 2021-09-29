package it.flowzz.ultimatetowny.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.models.Placeholder;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpMenu extends AbstractMenu implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = config.getConfigurationSection("menus.warp.items");
        Pagination pagination = contents.pagination();

        for (String slot : itemSection.getKeys(false)) {
            int index = getIndex(slot);
            if (index == -999)
                continue;
            contents.set(getSlot(index), ClickableItem.empty(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + "." + slot)));
        }

        contents.set(getSlot(itemSection.getInt("next-page.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".next-page.item"),
                event -> {
                    UltimateTownyPlugin.getInstance().getMenuHandler().getWarpMenu().open(player, pagination.next().getPage());
                    event.setCancelled(true);
                }));
        contents.set(getSlot(itemSection.getInt("previous-page.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".previous-page.item"),
                event -> {
                    UltimateTownyPlugin.getInstance().getMenuHandler().getWarpMenu().open(player, pagination.previous().getPage());
                    event.setCancelled(true);
                }));

        List<Town> towns = UltimateTownyPlugin.getInstance().getTownHandler().getTowns().values().stream()
                .filter(town -> town.getWarp() != null).toList();

        ClickableItem[] items = new ClickableItem[towns.size()];
        for (int i = 0; i < towns.size(); i++) {
            Town town = towns.get(i);
            items[i] = (ClickableItem.of(setPlayerSkin(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".town-item.item", new Placeholder("%town%", town.getName())), town.getLeader().getName()),
                    event -> {
                        Bukkit.dispatchCommand(event.getView().getPlayer(), "town warp " + town.getName());
                        event.setCancelled(true);
                    }));
        }
        pagination.setItems(items);
        pagination.setItemsPerPage(config.getInt("menus.warp.items-per-page", 27));
        SlotIterator slotIterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, getSlot(itemSection.getInt("town-item.slot")));
        slotIterator.allowOverride(false);
        pagination.addToIterator(slotIterator);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        //We do not need a dynamic menu
    }
}
