package it.flowzz.ultimatetowny.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.models.Placeholder;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.IntStream;

public class TownMenu extends AbstractMenu {

    private final Town town;

    public TownMenu(UltimateTownyPlugin plugin, Town town) {
        super(plugin);
        this.town = town;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = plugin.getConfig().getConfigurationSection("menus.town.items");
        for (String slot : itemSection.getKeys(false)) {
            int index = getIndex(slot);
            if (index == -999)
                continue;
            contents.set(getSlot(index), ClickableItem.empty(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + "." + slot)));
        }
        int position;
        List<Town> topTowns = plugin.getTownHandler().getTopTowns();
        position = IntStream.range(0, topTowns.size())
                .filter(i -> topTowns.get(i).getName().equalsIgnoreCase(town.getName()))
                .findFirst()
                .orElse(0);
        contents.set(getSlot(itemSection.getInt("stats.slot")), ClickableItem.empty(setPlayerSkin(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".stats.item",
                new Placeholder("%town_total_worth%", String.format("%d", Math.round(town.getTotalValue()))),
                new Placeholder("%town_coins%", String.format("%d", Math.round(town.getCoins()))),
                new Placeholder("%town_money%", String.format("%d", Math.round(town.getMoney()))),
                new Placeholder("%town_rating%", String.format("%.2f", Double.isNaN(town.getAverageRate()) ? 0.0 : town.getAverageRate())),
                new Placeholder("%town_position%", String.format("%d", position)),
                new Placeholder("%town_name%", town.getName())
        ), town.getLeader().getName())));
        contents.set(getSlot(itemSection.getInt("top.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".top.item"),
                event -> {
                    plugin.getMenuHandler().getTopMenu().open(player);
                    event.setCancelled(true);
                }));
        contents.set(getSlot(itemSection.getInt("help.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".help.item"),
                event -> {
                    Bukkit.dispatchCommand(player, "town help");
                    player.closeInventory();
                    event.setCancelled(true);
                }));
        contents.set(getSlot(itemSection.getInt("rating.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".rating.item"),
                event -> {
                    plugin.getMenuHandler().getRatingMenu().open(player);
                    event.setCancelled(true);
                }));
        contents.set(getSlot(itemSection.getInt("upgrade.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".upgrade.item"),
                event -> {
                    plugin.getMenuHandler().getUpgradeMenu(plugin, town).open(player);
                    event.setCancelled(true);
                }));
    }

}
