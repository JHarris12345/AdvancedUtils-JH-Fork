package net.advancedplugins.utils.hooks;

import com.google.common.collect.ImmutableMap;
import net.advancedplugins.utils.hooks.holograms.CMIHologramHandler;
import net.advancedplugins.utils.hooks.holograms.DecentHologramsHandler;
import net.advancedplugins.utils.hooks.holograms.HologramHandler;
import net.advancedplugins.utils.hooks.plugins.GriefPreventionHook;
import net.advancedplugins.utils.hooks.plugins.McMMOHook;
import net.advancedplugins.utils.hooks.plugins.SlimeFunHook;
import net.advancedplugins.utils.hooks.plugins.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HooksHandler {

    private static HologramHandler holograms;
    private static JavaPlugin plugin;

    private static ImmutableMap<HookPlugin, PluginHookInstance> pluginHookMap = ImmutableMap.<HookPlugin, PluginHookInstance>builder().build();

    public static void hook(JavaPlugin plugin) {
        HooksHandler.plugin = plugin;
        holograms();

        if (isPluginEnabled(HookPlugin.MCMMO.getPluginName()))
            registerNew(HookPlugin.MCMMO, new McMMOHook());

        if (isPluginEnabled(HookPlugin.WORLDGUARD.getPluginName()))
            registerNew(HookPlugin.WORLDGUARD, new WorldGuardHook());

        if (isPluginEnabled(HookPlugin.GRIEFPREVENTION.getPluginName()))
            registerNew(HookPlugin.GRIEFPREVENTION, new GriefPreventionHook());

        if (isPluginEnabled(HookPlugin.SLIMEFUN.getPluginName()))
            registerNew(HookPlugin.SLIMEFUN, new SlimeFunHook());
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
