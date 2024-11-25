package net.advancedplugins.utils.economy;


import net.advancedplugins.utils.economy.local.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;

public class EconomyHandler {

    private final HashMap<String, AdvancedEconomy> econMap = new HashMap<>();

    public EconomyHandler(JavaPlugin plugin) {
        registerEconomy(new ExpEconomy());
        registerEconomy(new LevelEconomy());
        registerEconomy(new DiamondsEconomy());
        registerEconomy(new EmeraldsEconomy());
        registerEconomy(new GoldEconomy());
        registerEconomy(new SoulsEconomy());
        if (plugin.getServer().getPluginManager().isPluginEnabled("Vault"))
            registerEconomy(new VaultEconomy());
    }

    public boolean charge(Player p, String price) {
        String econ = price.split(":")[0].toUpperCase(Locale.ROOT);
        double finalPrice = Double.parseDouble(price.split(":")[1]);
        return econMap.get(econ).chargeUser(p, finalPrice);
    }

    public AdvancedEconomy getEcon(String econ) {
        return econMap.get(econ.toUpperCase(Locale.ROOT));
    }

    public boolean registerEconomy(AdvancedEconomy economy) {
        String name = economy.getName().toUpperCase(Locale.ROOT);
        if (econMap.containsKey(name)) {
            return false;
        }

        econMap.put(name, economy);
        return true;
    }
}
