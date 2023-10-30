package net.advancedplugins.utils.economy.local;

import net.advancedplugins.utils.ExperienceManager;
import net.advancedplugins.utils.economy.AdvancedEconomy;
import org.bukkit.entity.Player;

public class ExpEconomy implements AdvancedEconomy {

    @Override
    public String getName() {
        return "EXP";
    }

    @Override
    public boolean chargeUser(Player p, double amount) {
        ExperienceManager exp = new ExperienceManager(p);
        if (exp.getTotalExperience() < amount)
            return false;

        exp.setTotalExperience((int) (exp.getTotalExperience() - amount));
        return true;
    }

    @Override
    public double getBalance(Player p) {
        return new ExperienceManager(p).getTotalExperience();
    }

    @Override
    public boolean giveUser(Player p, double bal) {
        ExperienceManager exp = new ExperienceManager(p);

        exp.setTotalExperience((int) (exp.getTotalExperience() - bal));
        return true;
    }
}
