package it.flowzz.ultimatetowny.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Placeholder;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.utils.ItemStackBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class RatingMenu extends AbstractMenu {

    public RatingMenu(UltimateTownyPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = plugin.getConfig().getConfigurationSection("menus.rating.items");
        Pagination pagination = contents.pagination();

        for (String slot : itemSection.getKeys(false)) {
            int index = getIndex(slot);
            if (index == -999)
                continue;
            contents.set(getSlot(index), ClickableItem.empty(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + "." + slot)));
        }

        contents.set(getSlot(itemSection.getInt("next-page.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".next-page.item"),
                event -> {
                    plugin.getMenuHandler().getRatingMenu().open(player, pagination.next().getPage());
                    event.setCancelled(true);
                }));
        contents.set(getSlot(itemSection.getInt("previous-page.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".previous-page.item"),
                event -> {
                    plugin.getMenuHandler().getRatingMenu().open(player, pagination.previous().getPage());
                    event.setCancelled(true);
                }));

        List<Town> towns = plugin.getTownHandler().getTowns().values().stream()
                .filter(town -> town.getWarp() != null).toList();

        ClickableItem[] items = new ClickableItem[towns.size()];
        for (int i = 0; i < towns.size(); i++) {
            Town town = towns.get(i);
            items[i] = ClickableItem.of(setPlayerSkin(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".town-item.item",
                            new Placeholder("%town%", town.getName()),
                            new Placeholder("%rating%", String.format("%.2f", Double.isNaN(town.getAverageRate()) ? 0.0 : town.getAverageRate()))
                    ), town.getLeader().getName()),
                    event -> {
                        if (event.getClick() == ClickType.LEFT) {
                            //Add Rate
                            if (town.hasRating(player.getUniqueId())) {
                                player.sendMessage(Messages.ALREADY_RATED.getTranslation());
                                player.closeInventory();
                            } else {
                                player.performCommand("town rate " + town.getName());
                            }
                        } else if (event.getClick() == ClickType.RIGHT) {
                            if (town.removeRating(player.getUniqueId())) {
                                player.sendMessage(Messages.RATING_REMOVED.getTranslation().replace("%town%", town.getName()));
                            } else {
                                player.sendMessage(Messages.NO_RATING.getTranslation());
                            }
                            player.closeInventory();
                        }
                        event.setCancelled(true);
                    });
        }
        pagination.setItems(items);
        pagination.setItemsPerPage(plugin.getConfig().getInt("menus.rating.items-per-page", 27));
        SlotIterator slotIterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, getSlot(itemSection.getInt("town-item.slot")));
        slotIterator.allowOverride(false);
        pagination.addToIterator(slotIterator);
    }
}
