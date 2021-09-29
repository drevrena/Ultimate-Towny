package it.flowzz.ultimatetowny.listeners;

import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.enums.UpgradeType;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.Upgrade;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final UltimateTownyPlugin plugin;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Upgrade expUpgrade = getExpUpgrade(event.getPlayer());
        if (expUpgrade == null)
            return;
        event.setExpToDrop((int) (event.getExpToDrop() * plugin.getConfig().getDouble("town.upgrade.experience.levels." + expUpgrade.getLevel() + ".booster")));
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null)
            return;
        Upgrade expUpgrade = getExpUpgrade(killer);
        if (expUpgrade == null)
            return;
        event.setDroppedExp((int) (event.getDroppedExp() * plugin.getConfig().getDouble("town.upgrade.experience.levels." + expUpgrade.getLevel() + ".booster")));
    }

    private Upgrade getExpUpgrade(Player player){
        Town town = plugin.getTownHandler().getTownByPlayer(player.getName());
        if (town == null)
            return null;
        return town.getUpgrade(UpgradeType.EXPERIENCE);
    }
}
