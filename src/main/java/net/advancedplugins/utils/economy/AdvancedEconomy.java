package net.advancedplugins.utils.economy;

import org.bukkit.entity.Player;

public interface AdvancedEconomy {
    String getName();
    boolean chargeUser(Player p, double amount);
    double getBalance(Player p);

    boolean giveUser(Player stealer, double bal);
}
