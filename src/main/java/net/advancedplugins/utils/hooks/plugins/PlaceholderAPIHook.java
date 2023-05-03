package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.PLACEHOLDERAPI.getPluginName();
    }

    public String parsePlaceholder(Player p, String s) {
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, s);
    }

}
