package it.flowzz.ultimatetowny.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.enums.UpgradeType;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Placeholder;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.Upgrade;
import it.flowzz.ultimatetowny.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class UpgradeMenu extends AbstractMenu {

    private final Town town;

    public UpgradeMenu(UltimateTownyPlugin plugin, Town town) {
        super(plugin);
        this.town = town;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        ConfigurationSection itemSection = plugin.getConfig().getConfigurationSection("menus.upgrade.items");
        for (String slot : itemSection.getKeys(false)) {
            int index = getIndex(slot);
            if (index == -999)
                continue;
            contents.set(getSlot(index), ClickableItem.empty(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + "." + slot)));
        }
        contents.set(getSlot(itemSection.getInt("members.slot")),
                ClickableItem.of(setPlayerSkin(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".members.item",
                                new Placeholder("%price_money%", UpgradeType.MEMBERS.getMaxLevel() == town.getUpgradeLevel(UpgradeType.MEMBERS) ? "N/A" :
                                        String.format("%d", plugin.getConfig().getInt("town.upgrades.members.levels." + (town.getUpgradeLevel(UpgradeType.MEMBERS) + 1) + ".money"))),
                                new Placeholder("%price_coins%", UpgradeType.MEMBERS.getMaxLevel() == town.getUpgradeLevel(UpgradeType.MEMBERS) ? "N/A" :
                                        String.format("%d", plugin.getConfig().getInt("town.upgrades.members.levels." + (town.getUpgradeLevel(UpgradeType.MEMBERS) + 1) + ".coins"))),
                                new Placeholder("%level%", String.format("%d", town.getUpgradeLevel(UpgradeType.MEMBERS)))), town.getLeader().getName()),
                        event -> {
                            upgrade(player, town, UpgradeType.MEMBERS);
                            event.setCancelled(true);
                        }));
        contents.set(getSlot(itemSection.getInt("experience.slot")),
                ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".experience.item",
                                new Placeholder("%price_money%", UpgradeType.EXPERIENCE.getMaxLevel() == town.getUpgradeLevel(UpgradeType.EXPERIENCE) ? "N/A" :
                                        String.format("%d", plugin.getConfig().getInt("town.upgrades.experience.levels." + (town.getUpgradeLevel(UpgradeType.EXPERIENCE) + 1) + ".money"))),
                                new Placeholder("%price_coins%", UpgradeType.EXPERIENCE.getMaxLevel() == town.getUpgradeLevel(UpgradeType.EXPERIENCE) ? "N/A" :
                                        String.format("%d", plugin.getConfig().getInt("town.upgrades.experience.levels." + (town.getUpgradeLevel(UpgradeType.EXPERIENCE) + 1) + ".coins"))),
                                new Placeholder("%level%", String.format("%d", town.getUpgradeLevel(UpgradeType.EXPERIENCE)))),
                        event -> {
                            upgrade(player, town, UpgradeType.EXPERIENCE);
                            event.setCancelled(true);
                        }));
        contents.set(getSlot(itemSection.getInt("money-gen.slot")),
                ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".money-gen.item",
                                new Placeholder("%price_money%", UpgradeType.MONEY_GEN.getMaxLevel() == town.getUpgradeLevel(UpgradeType.MONEY_GEN) ? "N/A" :
                                        String.format("%d", plugin.getConfig().getInt("town.upgrades.money-gen.levels." + (town.getUpgradeLevel(UpgradeType.MONEY_GEN) + 1) + ".money"))),
                                new Placeholder("%price_coins%", UpgradeType.MONEY_GEN.getMaxLevel() == town.getUpgradeLevel(UpgradeType.MONEY_GEN) ? "N/A" :
                                        String.format("%d", plugin.getConfig().getInt("town.upgrades.money-gen.levels." + (town.getUpgradeLevel(UpgradeType.MONEY_GEN) + 1) + ".coins"))),
                                new Placeholder("%level%", String.format("%d", town.getUpgradeLevel(UpgradeType.MONEY_GEN)))),
                        event -> {
                            upgrade(player, town, UpgradeType.MONEY_GEN);
                            event.setCancelled(true);
                        }));
        contents.set(getSlot(itemSection.getInt("coin-gen.slot")),
                ClickableItem.of(ItemStackBuilder.fromConfig(itemSection.getCurrentPath() + ".coin-gen.item",
                                new Placeholder("%price_money%", UpgradeType.COIN_GEN.getMaxLevel() == town.getUpgradeLevel(UpgradeType.COIN_GEN) ? "N/A" :
                                        String.format("%d", plugin.getConfig().getInt("town.upgrades.coin-gen.levels." + (town.getUpgradeLevel(UpgradeType.COIN_GEN) + 1) + ".money"))),
                                new Placeholder("%price_coins%", UpgradeType.COIN_GEN.getMaxLevel() == town.getUpgradeLevel(UpgradeType.COIN_GEN) ? "N/A" :
                                        String.format("%d", plugin.getConfig().getInt("town.upgrades.coin-gen.levels." + (town.getUpgradeLevel(UpgradeType.COIN_GEN) + 1) + ".coins"))),
                                new Placeholder("%level%", String.format("%d", town.getUpgradeLevel(UpgradeType.COIN_GEN)))),
                        event -> {
                            upgrade(player, town, UpgradeType.COIN_GEN);
                            event.setCancelled(true);
                        }));
    }

    private void upgrade(Player player, Town town, UpgradeType type) {
        Upgrade upgrade = town.getUpgrade(type);
        String upgradeName = type.name().toLowerCase().replace("_", "-");
        int currentLevel = upgrade == null ? 0 : upgrade.getLevel();
        int nextLevel = currentLevel + 1;
        if (currentLevel >= type.getMaxLevel()) {
            player.sendMessage(Messages.UPGRADE_MAX_LEVEL.getTranslation());
            player.closeInventory();
            return;
        }
        double moneyCost = plugin.getConfig().getDouble("town.upgrades." + upgradeName + ".levels." + nextLevel + ".money");
        double coinsCost = plugin.getConfig().getDouble("town.upgrades." + upgradeName + ".levels." + nextLevel + ".coins");
        if (town.getMoney() - moneyCost < 0) {
            player.sendMessage(Messages.NOT_ENOUGH_TOWN_MONEY.getTranslation());
            player.closeInventory();
            return;
        }
        if (town.getCoins() - coinsCost < 0) {
            player.sendMessage(Messages.NOT_ENOUGH_TOWN_COINS.getTranslation());
            player.closeInventory();
            return;
        }
        town.setMoney(town.getMoney() - moneyCost);
        town.setCoins(town.getCoins() - coinsCost);

        if (currentLevel == 0) {
            town.getUpgrades().add(new Upgrade(type, nextLevel));
        } else {
            upgrade.setLevel(nextLevel);
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(Messages.TOWN_UPGRADE.getTranslation()
                    .replace("%player%", player.getName())
                    .replace("%town%", town.getName())
                    .replace("%upgrade%", type.getTranslation())
                    .replace("%level%", String.valueOf(nextLevel))
            );
        }
        player.closeInventory();
    }
}
