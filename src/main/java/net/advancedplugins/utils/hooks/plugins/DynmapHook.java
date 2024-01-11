package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Bukkit;
import org.dynmap.DynmapAPI;

public class DynmapHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.DYNMAP.getPluginName();
    }

    public void setDynmapGeneration(boolean dynmapGeneration) {
        DynmapAPI dynmap = (DynmapAPI) Bukkit.getPluginManager().getPlugin(HookPlugin.DYNMAP.getPluginName());
        assert dynmap != null;
        dynmap.setPauseFullRadiusRenders(!dynmapGeneration);
    }

}
