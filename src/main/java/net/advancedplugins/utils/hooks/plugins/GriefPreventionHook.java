package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.GRIEFPREVENTION.getPluginName();
    }

    public boolean canBuild(Player p, Location l) {
        return true;
    }

}
