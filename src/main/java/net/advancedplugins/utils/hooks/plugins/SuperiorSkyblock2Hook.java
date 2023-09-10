package net.advancedplugins.utils.hooks.plugins;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;

public class SuperiorSkyblock2Hook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.SUPERIORSKYBLOCK2.getPluginName();
    }

    public int getStackedAmount(Block block) {
        return SuperiorSkyblockAPI.getStackedBlocks().getStackedBlockAmount(block);
    }

    public boolean isStackedBlock(Block block) {
        return getStackedAmount(block) > 1;
    }
}
