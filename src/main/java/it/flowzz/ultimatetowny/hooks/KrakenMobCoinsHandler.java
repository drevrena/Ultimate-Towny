package it.flowzz.ultimatetowny.hooks;

import it.flowzz.ultimatetowny.handlers.CoinHandler;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import org.bukkit.entity.Player;

public class KrakenMobCoinsHandler implements CoinHandler {

    @Override
    public int getBalance(Player player) {
        PlayerCoins playerData = MobCoins.getAPI().getPlayerData(player);
        return playerData == null ? 0 : (int) playerData.getMoney();
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        int balance = getBalance(player);
        if (balance - amount < 0){
            return false;
        }
        PlayerCoins playerData = MobCoins.getAPI().getPlayerData(player);
        if (playerData == null) {
            return false;
        }
        playerData.setMoney(balance - amount);
        return true;

    }

    @Override
    public void deposit(Player player, int amount) {
        PlayerCoins playerData = MobCoins.getAPI().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        playerData.setMoney(getBalance(player) + amount);
    }
}
