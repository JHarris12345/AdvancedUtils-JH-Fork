package net.advancedplugins.utils.hooks.plugins;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.OfflinePlayer;
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

    public String parsePlaceholder(OfflinePlayer p, String s) {
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, s);
    }

    public String parsePlaceholders(Player p1, Player p2, String s) {
        s = parsePlaceholder(p1, s);
        return me.clip.placeholderapi.PlaceholderAPI.setRelationalPlaceholders(p1, p2, s);
    }

    public boolean registerPlaceholder(String identifier, PlaceholderHook hook) {
        return PlaceholderAPI.registerPlaceholderHook(identifier, hook);
    }

}
