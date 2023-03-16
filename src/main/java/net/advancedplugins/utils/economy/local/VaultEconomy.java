package net.advancedplugins.utils.economy.local;

import net.advancedplugins.utils.ExperienceManager;
import net.advancedplugins.utils.economy.AdvancedEconomy;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements AdvancedEconomy {

    private Economy econ;

    public VaultEconomy() {
        setupEconomy();
    }

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null)
            return false;

        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public void giveUser(Player stealer, int bal) {
        econ.depositPlayer(stealer, bal);
    }

    @Override
    public String getName() {
        return "MONEY";
    }

    @Override
    public boolean chargeUser(Player p, double amount) {
        return econ.withdrawPlayer(p, amount).transactionSuccess();
    }

    @Override
    public double getBalance(Player p) {
        return econ.getBalance(p);
    }
}


