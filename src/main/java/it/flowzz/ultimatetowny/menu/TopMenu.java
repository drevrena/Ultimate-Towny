package it.flowzz.ultimatetowny.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.models.Placeholder;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import it.flowzz.ultimatetowny.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class TopMenu extends AbstractMenu implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = config.getConfigurationSection("menus.top.items");
        for (String slot : itemSection.getKeys(false)) {
            int index = getIndex(slot);
            if (index == -999)
                continue;
            contents.set(getSlot(index), ClickableItem.empty(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + "." + slot)));
        }
        List<Town> topTowns = UltimateTownyPlugin.getInstance().getTownHandler().getTopTowns();
        String[] positions = new String[]{"first", "second", "third", "fourth", "fifth"};
        for (int i = 0; i < positions.length; i++) {
            String position = positions[i];
            if (topTowns.size() - 1 < i) {
                continue;
            }
            Town town = topTowns.get(i);
            TownyPlayer first = getTopPlayer(town, 0);
            TownyPlayer second = getTopPlayer(town, 1);
            TownyPlayer third = getTopPlayer(town, 2);
            TownyPlayer fourth = getTopPlayer(town, 3);
            TownyPlayer fifth = getTopPlayer(town, 4);
            contents.set(getSlot(itemSection.getInt(position + ".slot")), ClickableItem.of(setPlayerSkin(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + "." + position + ".item",
                    new Placeholder("%town_name%", town.getName()),
                    new Placeholder("%town_value%", String.format("%d", Math.round(town.getTotalValue()))),
                    new Placeholder("%town_money%", String.format("%d", Math.round(town.getMoney()))),
                    new Placeholder("%town_coins%", String.format("%d", Math.round(town.getCoins()))),
                    new Placeholder("%top_1_name%", first == null ? "" : first.getName()),
                    new Placeholder("%top_2_name%", second == null ? "" : second.getName()),
                    new Placeholder("%top_3_name%", third == null ? "" : third.getName()),
                    new Placeholder("%top_4_name%", fourth == null ? "" : fourth.getName()),
                    new Placeholder("%top_5_name%", fifth == null ? "" : fifth.getName())
                    ), town.getLeader().getName()), event -> {
                Bukkit.dispatchCommand(player, "town warp " + town.getName());
                player.closeInventory();
            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        //We do not need a dynamic menu
    }

    private TownyPlayer getTopPlayer(Town town, int position) {
        List<TownyPlayer> topPlayers = UltimateTownyPlugin.getInstance().getTownHandler().getTopPlaytime().get(town);
        if (topPlayers.size() - 1 >= position)
            return topPlayers.get(position);
        return null;
    }

}
