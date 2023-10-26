package net.advancedplugins.utils.hooks.plugins;

import io.th0rgal.oraxen.api.OraxenBlocks;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
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
}
