package it.flowzz.ultimatetowny.tasks;

import it.flowzz.ultimatetowny.UltimateTownyPlugin;
import it.flowzz.ultimatetowny.enums.UpgradeType;
import it.flowzz.ultimatetowny.lang.Messages;
import it.flowzz.ultimatetowny.models.Town;
import it.flowzz.ultimatetowny.models.TownyPlayer;
import it.flowzz.ultimatetowny.models.Upgrade;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CoinGenTask implements Runnable {

    private final UltimateTownyPlugin plugin;

    @Override
    public void run() {
        for (Town town : plugin.getTownHandler().getTowns().values()) {
            Upgrade coinUp = town.getUpgrade(UpgradeType.COIN_GEN);
            if (coinUp != null) {
                int amount = plugin.getConfig().getInt("town.upgrades.coin-gen.levels." + coinUp.getLevel() + ".amount");
                town.setCoins(town.getCoins() + amount);
                for (TownyPlayer townPlayer : town.getPlayers()) {
                    Player player = townPlayer.getPlayer();
                    if (player != null)
                        player.sendMessage(Messages.COINS_GENERATED.getTranslation().replace("%amount%", String.format("%d", amount)));
                }
            }
        }
    }
}
