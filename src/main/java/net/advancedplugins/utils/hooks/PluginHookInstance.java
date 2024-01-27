package net.advancedplugins.utils.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginHookInstance {

    public boolean isEnabled() {
        return false;
    }

    public String getName() {
        return "";
    }

    public Plugin getPluginInstance() {
        return Bukkit.getPluginManager().getPlugin(getName());
    }
}
