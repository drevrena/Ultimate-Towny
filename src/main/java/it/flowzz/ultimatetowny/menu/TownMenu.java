package it.flowzz.ultimatetowny.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.models.Placeholder;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.utils.ItemStackBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class TownMenu extends AbstractMenu implements InventoryProvider {

    private final Town town;

    @Override
    public void init(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = config.getConfigurationSection("menus.town.items");
        for (String slot : itemSection.getKeys(false)) {
            int index = getIndex(slot);
            if (index == -999)
                continue;
            contents.set(getSlot(index), ClickableItem.empty(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + "." + slot)));
        }
        int position;
        List<Town> topTowns = UltimateTownyPlugin.getInstance().getTownHandler().getTopTowns();
        position = IntStream.range(0, topTowns.size())
                .filter(i -> topTowns.get(i).getName().equalsIgnoreCase(town.getName()))
                .findFirst()
                .orElse(0);
        contents.set(getSlot(itemSection.getInt("stats.slot")), ClickableItem.empty(setPlayerSkin(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".stats.item",
                new Placeholder("%town_total_worth%", String.format("%d", Math.round(town.getTotalValue()))),
                new Placeholder("%town_coins%", String.format("%d", Math.round(town.getCoins()))),
                new Placeholder("%town_money%", String.format("%d", Math.round(town.getMoney()))),
                new Placeholder("%town_position%", String.format("%d", position)),
                new Placeholder("%town_name%", town.getName())
        ), town.getLeader().getName())));
        contents.set(getSlot(itemSection.getInt("top.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".top.item"),
                event -> {
                    UltimateTownyPlugin.getInstance().getMenuHandler().getTopMenu().open(player);
                    event.setCancelled(true);
                }));
        contents.set(getSlot(itemSection.getInt("help.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".help.item"),
                event -> {
                    Bukkit.dispatchCommand(player, "town help");
                    player.closeInventory();
                    event.setCancelled(true);
                }));
        contents.set(getSlot(itemSection.getInt("upgrade.slot")), ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".upgrade.item"),
                event -> {
                    UltimateTownyPlugin.getInstance().getMenuHandler().getUpgradeMenu(town).open(player);
                    event.setCancelled(true);
                }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        //We do not need a dynamic menu
    }

}
