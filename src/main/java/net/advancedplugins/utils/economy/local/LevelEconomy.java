package net.advancedplugins.utils.economy.local;

import net.advancedplugins.utils.economy.AdvancedEconomy;
import org.bukkit.entity.Player;

public class LevelEconomy implements AdvancedEconomy {

    @Override
    public String getName() {
        return "LEVEL";
    }

    @Override
    public boolean chargeUser(Player p, double amount) {
        if (p.getLevel() < amount)
            return false;

        p.setLevel((int) (p.getLevel() - amount));
        return true;
    }

    @Override
    public double getBalance(Player p) {
        return p.getLevel();
    }

    @Override
    public boolean giveUser(Player p, double bal) {
        p.setLevel((int) (p.getLevel() + bal));
        return true;
    }
}
