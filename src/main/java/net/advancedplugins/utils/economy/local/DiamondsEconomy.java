package net.advancedplugins.utils.economy.local;

import net.advancedplugins.ae.utils.AManager;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.economy.AdvancedEconomy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class DiamondsEconomy implements AdvancedEconomy {

    @Override
    public String getName() {
        return "DIAMONDS";
    }

    @Override
    public boolean chargeUser(Player p, double amount) {
        if(!AManager.hasAmount(p, Material.DIAMOND, (int) amount))
            return false;

        AManager.removeItems(p.getInventory(), Material.DIAMOND, (int) amount);
        return true;
    }

    @Override
    public double getBalance(Player p) {
        return AManager.getAmount(p, Material.DIAMOND);
    }

    @Override
    public boolean giveUser(Player p, double bal) {
        ASManager.giveItem(p, Collections.nCopies((int) bal, Material.DIAMOND).stream()
                .map(ItemStack::new)
                .toArray(ItemStack[]::new)
        );
        return true;
    }
}
