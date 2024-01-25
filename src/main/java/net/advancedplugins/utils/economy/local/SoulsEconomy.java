package net.advancedplugins.utils.economy.local;

import net.advancedplugins.ae.features.souls.SoulsAPI;
import net.advancedplugins.ae.utils.ItemInHand;
import net.advancedplugins.utils.economy.AdvancedEconomy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoulsEconomy implements AdvancedEconomy {

    @Override
    public String getName() {
        return "SOULS";
    }

    @Override
    public boolean chargeUser(Player p, double amount) {
        ItemStack itemInHand = new ItemInHand(p).get();
        if (getBalance(p) < amount)
            return false;

        new ItemInHand(p).set(SoulsAPI.useSouls(itemInHand, ((int) amount)));
        return true;
    }

    @Override
    public double getBalance(Player p) {
        ItemStack itemInHand = new ItemInHand(p).get();
        return SoulsAPI.getSoulsOnItem(itemInHand);
    }

    @Override
    public boolean giveUser(Player p, double bal) {
        ItemStack itemInHand = new ItemInHand(p).get();
        new ItemInHand(p).set(SoulsAPI.addSouls(itemInHand, ((int) bal)));
        return true;
    }
}
