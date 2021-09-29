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
public class MoneyGenTask implements Runnable {

    private final UltimateTownyPlugin plugin;

    @Override
    public void run() {
        for (Town town : plugin.getTownHandler().getTowns().values()) {
            Upgrade moneyUp = town.getUpgrade(UpgradeType.MONEY_GEN);
            if (moneyUp != null) {
                int amount = plugin.getConfig().getInt("town.upgrades.money-gen.levels." + moneyUp.getLevel() + ".amount");
                town.setMoney(town.getMoney() + amount);
                for (TownyPlayer townPlayer : town.getPlayers()) {
                    Player player = townPlayer.getPlayer();
                    if (player != null)
                        player.sendMessage(Messages.MONEY_GENERATED.getTranslation().replace("%amount%", String.format("%d", amount)));
                }
            }
        }
    }
}
