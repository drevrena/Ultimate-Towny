package it.flowzz.ultimatetowny.handlers;

import org.bukkit.entity.Player;

public interface CoinHandler {

    int getBalance(Player player);

    boolean withdraw(Player player, int amount);

    void deposit(Player player, int amount);
}
