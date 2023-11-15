package net.advancedplugins.utils.economy.local;

import net.advancedplugins.utils.economy.AdvancedEconomy;
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
    public boolean giveUser(Player stealer, double bal) {
        return econ.depositPlayer(stealer, bal).transactionSuccess();
    }

    @Override
    public String getName() {
        return "MONEY";
    }

    @Override
    public boolean chargeUser(Player p, double amount) {
        if (this.getBalance(p) < amount) {
            return false;
        }
        return econ.withdrawPlayer(p, amount).transactionSuccess();
    }

    @Override
    public double getBalance(Player p) {
        return econ.getBalance(p);
    }
}


