package net.advancedplugins.utils.economy.local;

import net.advancedplugins.ae.utils.AManager;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.economy.AdvancedEconomy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class GoldEconomy implements AdvancedEconomy {

    @Override
    public String getName() {
        return "GOLD";
    }

    @Override
    public boolean chargeUser(Player p, double amount) {
        if(!AManager.hasAmount(p, Material.GOLD_INGOT, (int) amount))
            return false;

        AManager.removeItems(p.getInventory(), Material.GOLD_INGOT, (int) amount);
        return true;
    }

    @Override
    public double getBalance(Player p) {
        return AManager.getAmount(p, Material.GOLD_INGOT);
    }

    @Override
    public boolean giveUser(Player p, double bal) {
        ASManager.giveItem(p, Collections.nCopies((int) bal, Material.GOLD_INGOT).stream()
                .map(ItemStack::new)
                .toArray(ItemStack[]::new)
        );
        return true;
    }
}
