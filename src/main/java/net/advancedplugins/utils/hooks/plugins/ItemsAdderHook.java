package net.advancedplugins.utils.hooks.plugins;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
        return CustomStack.byItemStack(item) != null;
        // deprecated
       // return ItemsAdder.isCustomItem(item);
    }

    public boolean isCustomBlock(Block block) {
        return CustomBlock.byAlreadyPlaced(block) != null;
        // deprecated
      //  return ItemsAdder.isCustomBlock(block);
    }

    public List<ItemStack> getLootForCustomBlock(Block block) {
        if (!isCustomBlock(block)) return null;
        return (List<ItemStack>) CustomBlock.byAlreadyPlaced(block).getLoot();
    }

    public ItemStack setCustomItemDurability(ItemStack item, int durability) {
        CustomStack stack = CustomStack.byItemStack(item);
        stack.setDurability(durability);
        return stack.getItemStack();
    }

    public ItemStack getByName(String name) {
        CustomStack stack = CustomStack.getInstance(name);
        if (stack == null)
            return null;
        return stack.getItemStack();
    }

    public int getCustomItemDurability(ItemStack itemStack) {
        return CustomStack.byItemStack(itemStack).getDurability();
    }

    public int getCustomItemMaxDurability(ItemStack itemStack) {
        return CustomStack.byItemStack(itemStack).getMaxDurability();
    }
}
