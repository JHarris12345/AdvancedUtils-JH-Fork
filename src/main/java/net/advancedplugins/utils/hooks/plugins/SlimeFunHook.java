package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

}
