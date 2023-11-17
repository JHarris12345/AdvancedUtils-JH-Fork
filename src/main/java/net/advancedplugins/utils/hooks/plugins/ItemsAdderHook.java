package net.advancedplugins.utils.hooks.plugins;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.SchedulerUtils;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ItemsAdderHook extends PluginHookInstance implements Listener {

    private final Plugin plugin;

    public ItemsAdderHook(Plugin plugin) {
        this.plugin = plugin;
    }

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
    }

    public boolean isCustomBlock(Block block) {
        if (block == null) return false;
        return CustomBlock.byAlreadyPlaced(block) != null;
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomBlockBreak(CustomBlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        // otherwise the item is put in the inventory and at the same time is
        if (block.hasMetadata("telepathy-broken-itemsadder")) {
            block.removeMetadata("telepathy-broken-itemsadder", plugin);
            event.setCancelled(true);
            SchedulerUtils.runTaskLater(() -> {
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                ItemStack[] drops = ((List<ItemStack>) customBlock.getLoot(player.getEquipment().getItemInMainHand(), false)).toArray(new ItemStack[0]);
                if (customBlock.remove()) {
                    ASManager.giveItem(player, drops);
                }
            });
        }
    }
}
