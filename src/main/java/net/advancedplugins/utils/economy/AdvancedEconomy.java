package net.advancedplugins.utils.economy;

import org.bukkit.entity.Player;

public interface AdvancedEconomy {

    public String getName();

    public boolean chargeUser(Player p, double amount);
    public double getBalance(Player p);

    void giveUser(Player stealer, int bal);
}
