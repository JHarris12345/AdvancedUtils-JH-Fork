package net.advancedplugins.utils.hooks.plugins;

import io.th0rgal.oraxen.api.OraxenBlocks;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

public class OraxenHook extends PluginHookInstance implements Listener {

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
