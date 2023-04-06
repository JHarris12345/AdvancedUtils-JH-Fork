package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.PluginHookInstance;

public class WorldGuardHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "WorldGuardHook";
    }

}
