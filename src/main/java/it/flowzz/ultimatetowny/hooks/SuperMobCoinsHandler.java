package it.flowzz.ultimatetowny.hooks;

import it.flowzz.ultimatetowny.handlers.CoinHandler;
import me.swanis.mobcoins.MobCoinsAPI;
import org.bukkit.entity.Player;

public class SuperMobCoinsHandler implements CoinHandler {

    @Override
    public int getBalance(Player player) {
        return MobCoinsAPI.getProfileManager().getProfile(player).getMobCoins();
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        int balance = getBalance(player);
        if (balance - amount < 0) {
            return false;
        }
        MobCoinsAPI.getProfileManager().getProfile(player).setMobCoins(getBalance(player) - amount);
        return true;
    }

    @Override
    public void deposit(Player player, int amount) {
        MobCoinsAPI.getProfileManager().getProfile(player).setMobCoins(getBalance(player) + amount);
    }
}
