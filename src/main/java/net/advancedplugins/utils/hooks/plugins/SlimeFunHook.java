package net.advancedplugins.utils.hooks.plugins;

import me.mrCookieSlime.Slimefun.api.BlockStorage;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class SlimeFunHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.SLIMEFUN.getPluginName();
    }

    public boolean canBuild(Player p, Location l) {
        return me.mrCookieSlime.Slimefun.api.BlockStorage.check(l.getBlock()) == null;
    }

    public boolean isSlimefunItem(Location loc) {
        return BlockStorage.check(loc) != null;
    }

    public boolean isSlimefunItem(Block block) {
        return BlockStorage.check(block) != null;
    }

    public Collection<ItemStack> getDrops(Location loc) {
        return BlockStorage.check(loc).getDrops();
    }
}
