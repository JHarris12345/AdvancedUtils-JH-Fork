package net.advancedplugins.utils.hooks.plugins;

import dev.espi.protectionstones.ProtectionStones;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ProtectionStonesHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.PROTECTIONSTONES.getPluginName();
    }

    public boolean isProtectionStone(@NotNull Block block) {
        return ProtectionStones.isProtectBlock(block);
    }
}
