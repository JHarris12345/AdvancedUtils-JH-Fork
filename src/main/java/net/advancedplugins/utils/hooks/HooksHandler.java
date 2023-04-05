package net.advancedplugins.utils.hooks;

import com.google.common.collect.ImmutableMap;
import net.advancedplugins.utils.hooks.holograms.CMIHologramHandler;
import net.advancedplugins.utils.hooks.holograms.DecentHologramsHandler;
import net.advancedplugins.utils.hooks.holograms.HologramHandler;
import net.advancedplugins.utils.hooks.plugins.McMMOHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HooksHandler {

    private static HologramHandler holograms;
    private static JavaPlugin plugin;

    private static ImmutableMap<HookPlugin, PluginHookInstance> pluginHookMap = ImmutableMap.<HookPlugin, PluginHookInstance>builder().build();

    public static void hook(JavaPlugin plugin) {
        HooksHandler.plugin = plugin;
        holograms();

        if (isPluginEnabled(HookPlugin.MCMMO.getPluginName())) {
            registerNew(HookPlugin.MCMMO, new McMMOHook());
        }
    }

    private static void registerNew(HookPlugin plugin, PluginHookInstance instance) {
        pluginHookMap = ImmutableMap.<HookPlugin, PluginHookInstance>builder().putAll(pluginHookMap)
                .put(plugin, instance).build();
    }

    public static PluginHookInstance getHook(HookPlugin plugin) {
        return pluginHookMap.getOrDefault(plugin, new PluginHookInstance());
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
        } else {
            plugin.getLogger().info("No hologram plugin was detected - spawner holograms will be disabled.");
            holograms = new HologramHandler(plugin);
        }
    }

    private static boolean isPluginEnabled(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    public static HologramHandler getHolograms() {
        return holograms;
    }

}
