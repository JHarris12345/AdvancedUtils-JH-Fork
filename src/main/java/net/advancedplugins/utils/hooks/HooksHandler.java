package net.advancedplugins.utils.hooks;

import net.advancedplugins.utils.hooks.holograms.CMIHologramHandler;
import net.advancedplugins.utils.hooks.holograms.DecentHologramsHandler;
import net.advancedplugins.utils.hooks.holograms.HologramHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HooksHandler {

    private static HologramHandler holograms;
    private static JavaPlugin plugin;

    public static void hook(JavaPlugin plugin) {
        HooksHandler.plugin = plugin;
        holograms();
    }

    private static void holograms() {
        if (isPluginEnabled("CMI")) {
            holograms = new CMIHologramHandler(plugin);
            return;
        } else if (isPluginEnabled("DecentHolograms")) {
            holograms = new DecentHologramsHandler(plugin);
            return;
        } else if (isPluginEnabled("HolographicDisplays")) {
            holograms = new DecentHologramsHandler(plugin);
            return;
        }
    }

    private static boolean isPluginEnabled(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    public static HologramHandler getHolograms() {
        return holograms;
    }


}
