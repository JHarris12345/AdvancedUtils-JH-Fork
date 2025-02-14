package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.ae.api.AEAPI;
import net.advancedplugins.ae.enchanthandler.enchantments.AdvancedEnchantment;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class AdvancedEnchantmentsHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "AdvancedEnchantments";
    }

    public HashMap<String, Integer> getEnchantmentsOnItem(ItemStack item) {
        return AEAPI.getEnchantmentsOnItem(item);
    }

    public List<String> getAllEnchantments() {
        return AEAPI.getAllEnchantments();
    }

    public AdvancedEnchantment getInstance(String enchantment) {
        return AEAPI.getEnchantmentInstance(enchantment);
    }
}
