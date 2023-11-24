package net.advancedplugins.utils.hooks.plugins;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.events.noteblock.OraxenNoteBlockBreakEvent;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanic;
import io.th0rgal.oraxen.utils.drops.Loot;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class OraxenHook extends PluginHookInstance implements Listener {
    private final Plugin plugin;

    public OraxenHook(Plugin plugin) {
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

    public boolean isCustomBlock(Block block) {
        return OraxenBlocks.isOraxenBlock(block);
    }

    public boolean isCustomStringBlock(Block block) {
        return OraxenBlocks.isOraxenStringBlock(block);
    }

    public boolean isCustomNoteBlock(Block block) {
        return OraxenBlocks.isOraxenNoteBlock(block);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomBlockBreak(OraxenNoteBlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        // otherwise the item is put in the inventory and at the same time is
        if (block.hasMetadata("telepathy-broken-oraxen")) {
            block.removeMetadata("telepathy-broken-oraxen", plugin);
            event.setCancelled(true);

            ItemStack[] loot = new ItemStack[0];
            if (isCustomNoteBlock(block)) {
                NoteBlockMechanic customBlock = OraxenBlocks.getNoteBlockMechanic(block.getBlockData());
                loot = customBlock.getDrop().getLoots().stream()
                        .map(Loot::getItemStack)
                        .toArray(ItemStack[]::new);
            } else if (isCustomStringBlock(block)) {
                StringBlockMechanic customBlock = OraxenBlocks.getStringMechanic(block.getBlockData());
                loot = customBlock.getDrop().getLoots().stream()
                        .map(Loot::getItemStack)
                        .toArray(ItemStack[]::new);
            }

            // if player is not null, the remove method will drop item automatically
            if (OraxenBlocks.remove(block.getLocation(), null)) {
                ASManager.giveItem(player, loot);
            }
        }
    }
}
