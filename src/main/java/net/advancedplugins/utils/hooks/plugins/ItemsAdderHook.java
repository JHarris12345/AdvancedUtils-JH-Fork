package net.advancedplugins.utils.hooks.plugins;

import dev.lone.itemsadder.api.ItemsAdder;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.ITEMSADDER.getPluginName();
    }

    public boolean isCustomItem(ItemStack item) {
        return ItemsAdder.isCustomItem(item);
    }

    public ItemStack setCustomItemDurability(ItemStack item, int durability) {
        return ItemsAdder.setCustomItemDurability(item, durability);
    }

    public int getCustomItemDurability(ItemStack itemStack) {
        return ItemsAdder.getCustomItemDurability(itemStack);
    }

    public int getCustomItemMaxDurability(ItemStack itemStack) {
        return ItemsAdder.getCustomItemMaxDurability(itemStack);
    }

}
