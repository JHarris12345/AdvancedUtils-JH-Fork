package net.advancedplugins.utils.hooks.plugins;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.PLACEHOLDERAPI.getPluginName();
    }

    public String parsePlaceholder(OfflinePlayer p, String s) {
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, s);
    }

    public boolean registerPlaceholder(String identifier, PlaceholderHook hook) {
        return PlaceholderAPI.registerPlaceholderHook(identifier, hook);
    }

}
