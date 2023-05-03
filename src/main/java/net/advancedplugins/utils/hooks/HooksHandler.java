package net.advancedplugins.utils.hooks;

import com.google.common.collect.ImmutableMap;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.hooks.holograms.CMIHologramHandler;
import net.advancedplugins.utils.hooks.holograms.DecentHologramsHandler;
import net.advancedplugins.utils.hooks.holograms.HologramHandler;
import net.advancedplugins.utils.hooks.plugins.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

public class HooksHandler {

    private static HologramHandler holograms;
    private static JavaPlugin plugin;

    private static ImmutableMap<HookPlugin, PluginHookInstance> pluginHookMap = ImmutableMap.<HookPlugin, PluginHookInstance>builder().build();

    public static void hook(JavaPlugin plugin) {
        // This shouldn't be loaded more than once, but if it is - clear pluginHookMap
        if (!pluginHookMap.isEmpty()) {
            pluginHookMap = ImmutableMap.<HookPlugin, PluginHookInstance>builder().build();
        }

        HooksHandler.plugin = plugin;
        holograms();

        if (isPluginEnabled(HookPlugin.MCMMO.getPluginName()))
            registerNew(HookPlugin.MCMMO, new McMMOHook());

        if (isPluginEnabled(HookPlugin.AURELIUMSKILLS.getPluginName()))
            registerNew(HookPlugin.AURELIUMSKILLS, new AureliumSkillsHook(), true);

        if (isPluginEnabled(HookPlugin.WORLDGUARD.getPluginName()))
            registerNew(HookPlugin.WORLDGUARD, new WorldGuardHook());

        if (isPluginEnabled(HookPlugin.GRIEFPREVENTION.getPluginName()))
            registerNew(HookPlugin.GRIEFPREVENTION, new GriefPreventionHook());

        if (isPluginEnabled(HookPlugin.PLACEHOLDERAPI.getPluginName()))
            registerNew(HookPlugin.PLACEHOLDERAPI, new PlaceholderAPIHook());

        if (isPluginEnabled(HookPlugin.SLIMEFUN.getPluginName()))
            registerNew(HookPlugin.SLIMEFUN, new SlimeFunHook());

        // Figure out which factions plugin is loaded and hook into the correct one

        if (isPluginEnabled(HookPlugin.FACTIONS.getPluginName())) {
            if (isPluginEnabled("MassiveCore")) {
                registerNew(HookPlugin.FACTIONS, new FactionsMCoreHook());
            } else {
                registerNew(HookPlugin.FACTIONS, new FactionsUUIDHook());
            }
        }

        sendHookMessage(plugin);
    }

    private static void sendHookMessage(JavaPlugin plugin) {
        if (pluginHookMap.isEmpty())
            return;

        StringBuilder hooks = new StringBuilder();
        for (HookPlugin hook : pluginHookMap.keySet())
            hooks.append(hook.getPluginName()).append(", ");

        plugin.getLogger().info("Successfully hooked into " + hooks.substring(0, hooks.length() - 2) + ".");
    }


    private static void registerNew(HookPlugin plugin, PluginHookInstance instance) {
        registerNew(plugin, instance, false);
    }


    private static void registerNew(HookPlugin plugin, PluginHookInstance instance, boolean listener) {
        pluginHookMap = ImmutableMap.<HookPlugin, PluginHookInstance>builder().putAll(pluginHookMap)
                .put(plugin, instance).build();

        if (listener) {
            HooksHandler.plugin.getServer().getPluginManager().registerEvents((Listener) instance, HooksHandler.plugin);
        }
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

    public static boolean isEnabled(HookPlugin hookPlugin) {
        return pluginHookMap.containsKey(hookPlugin);
    }
}
