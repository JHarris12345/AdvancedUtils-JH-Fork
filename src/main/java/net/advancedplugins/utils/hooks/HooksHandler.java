package net.advancedplugins.utils.hooks;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.advancedplugins.utils.FoliaScheduler;
import net.advancedplugins.utils.hooks.holograms.CMIHologramHandler;
import net.advancedplugins.utils.hooks.holograms.DecentHologramsHandler;
import net.advancedplugins.utils.hooks.holograms.HologramHandler;
import net.advancedplugins.utils.hooks.plugins.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class HooksHandler {
    @Getter
    private static HologramHandler holograms;
    private static JavaPlugin plugin;

    private static ImmutableMap<HookPlugin, PluginHookInstance> pluginHookMap = ImmutableMap.<HookPlugin, PluginHookInstance>builder().build();

    public static void hook(JavaPlugin plugin) {
        // This shouldn't be loaded more than once, but if it is - clear pluginHookMap
        if (!pluginHookMap.isEmpty())
            pluginHookMap = ImmutableMap.<HookPlugin, PluginHookInstance>builder().build();

        HooksHandler.plugin = plugin;
        holograms();

        if (isPluginEnabled(HookPlugin.PROTOCOLLIB.getPluginName()))
            registerNew(HookPlugin.PROTOCOLLIB, new PluginHookInstance()); // Generic plugin hook

        // AureliumSKills hook must be loaded instantly without runnable
        if (isPluginEnabled(HookPlugin.AURASKILLS.getPluginName()))
            registerNew(HookPlugin.AURASKILLS, new AuraSkillsHook(), true);

        // these should be fine loading rn as well, as they are softdepend in plugin.yml (Wega)
        if (isPluginEnabled(HookPlugin.MCMMO.getPluginName()))
            registerNew(HookPlugin.MCMMO, new McMMOHook(), true);

        if (isPluginEnabled(HookPlugin.ADVANCEDENCHANTMENTS.getPluginName()))
            registerNew(HookPlugin.ADVANCEDENCHANTMENTS, new AdvancedEnchantmentsHook());

        if (isPluginEnabled(HookPlugin.ADVANCEDSKILLS.getPluginName()))
            registerNew(HookPlugin.ADVANCEDSKILLS, new AdvancedSkillsHook());

        if (isPluginEnabled(HookPlugin.WORLDGUARD.getPluginName()))
            registerNew(HookPlugin.WORLDGUARD, new WorldGuardHook());

        if (isPluginEnabled(HookPlugin.GRIEFPREVENTION.getPluginName()))
            registerNew(HookPlugin.GRIEFPREVENTION, new GriefPreventionHook());

        if (isPluginEnabled(HookPlugin.GRIEFDEFENDER.getPluginName()))
            registerNew(HookPlugin.GRIEFDEFENDER, new GriefDefenderHook());

        if (isPluginEnabled(HookPlugin.PLACEHOLDERAPI.getPluginName()))
            registerNew(HookPlugin.PLACEHOLDERAPI, new PlaceholderAPIHook());

        if (isPluginEnabled(HookPlugin.SLIMEFUN.getPluginName()))
            registerNew(HookPlugin.SLIMEFUN, new SlimeFunHook());

        if (isPluginEnabled(HookPlugin.MYTHICMOBS.getPluginName()))
            registerNew(HookPlugin.MYTHICMOBS, new MythicMobsHook(), true);

        if (isPluginEnabled(HookPlugin.TOWNY.getPluginName()))
            registerNew(HookPlugin.TOWNY, new TownyHook());

        if (isPluginEnabled(HookPlugin.TOWNYCHAT.getPluginName()))
            registerNew(HookPlugin.TOWNYCHAT, new TownyChatHook());

        if (isPluginEnabled(HookPlugin.LANDS.getPluginName()))
            registerNew(HookPlugin.LANDS, new LandsHook());

        if (isPluginEnabled(HookPlugin.SUPERIORSKYBLOCK2.getPluginName()))
            registerNew(HookPlugin.SUPERIORSKYBLOCK2, new SuperiorSkyblock2Hook());

        if (isPluginEnabled(HookPlugin.ORAXEN.getPluginName()))
            registerNew(HookPlugin.ORAXEN, new OraxenHook(plugin), true);

        if (isPluginEnabled(HookPlugin.PROTECTIONSTONES.getPluginName()))
            registerNew(HookPlugin.PROTECTIONSTONES, new ProtectionStonesHook());

        if (isPluginEnabled(HookPlugin.RESIDENCE.getPluginName()))
            registerNew(HookPlugin.RESIDENCE, new ResidenceHook());

        if (isPluginEnabled(HookPlugin.GEYSER.getPluginName()))
            registerNew(HookPlugin.GEYSER, new GeyserHook());

        if (isPluginEnabled(HookPlugin.DYNMAP.getPluginName()))
            registerNew(HookPlugin.DYNMAP, new DynmapHook());

        if (isPluginEnabled(HookPlugin.ESSENTIALS.getPluginName()))
            registerNew(HookPlugin.ESSENTIALS, new EssentialsHook());

        if (isPluginEnabled(HookPlugin.CMI.getPluginName()))
            registerNew(HookPlugin.CMI, new CMIHook());

        if (isPluginEnabled(HookPlugin.BEACONPLUS3.getPluginName()))
            registerNew(HookPlugin.BEACONPLUS3, new BeaconsPlus3Hook());

        if (isPluginEnabled(HookPlugin.VAULT.getPluginName()))
            registerNew(HookPlugin.VAULT, new VaultHook());

        if (isPluginEnabled(HookPlugin.LUCKPERMS.getPluginName()))
            registerNew(HookPlugin.LUCKPERMS, new LuckPermsHook());

        if (isPluginEnabled(HookPlugin.SUPERVANISH.getPluginName()))
            registerNew(HookPlugin.SUPERVANISH, new SuperVanishHook());

        if (isPluginEnabled(HookPlugin.VIAVERSION.getPluginName()))
            registerNew(HookPlugin.VIAVERSION, new ViaVersionHook());

        // Do this after server is loaded, so all softdepends that aren't in the plugin.yml file will be enabled by this time
        FoliaScheduler.runTaskLater(plugin, () -> {
            // Figure out which factions plugin is loaded and hook into the correct one

            if (isPluginEnabled(HookPlugin.FACTIONS.getPluginName())) {
                if (isPluginEnabled("MassiveCore")) {
                    registerNew(HookPlugin.FACTIONS, new FactionsMCoreHook());
                } else {
                    registerNew(HookPlugin.FACTIONS, new FactionsUUIDHook());
                }
            }

            if (isPluginEnabled(HookPlugin.ITEMSADDER.getPluginName()))
                registerNew(HookPlugin.ITEMSADDER, new ItemsAdderHook(plugin), true);

            if (isPluginEnabled(HookPlugin.ADVANCEDCHESTS.getPluginName()))
                registerNew(HookPlugin.ADVANCEDCHESTS, new AdvancedChestsHook());

            // needs to be run later and not declared in plugin.yml, otherwise circular dependency will happen
            if (isPluginEnabled(HookPlugin.PREMIUMVANISH.getPluginName()))
                registerNew(HookPlugin.PREMIUMVANISH, new PremiumVanishHook());

            // doesn't work correctly even if in softdepends if not here.
            // My guess is that it's because discordsrv is dependent in Essentials and that overrides our softdepend
            if (isPluginEnabled(HookPlugin.DISCORDSRV.getPluginName()))
                registerNew(HookPlugin.DISCORDSRV, new DiscordSRVHook());

            sendHookMessage(plugin);
        }, 10);
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
        return pluginHookMap.get(plugin);
    }

    private static void holograms() {
        if (isPluginEnabled("CMI")) {
            holograms = new CMIHologramHandler(plugin);
        } else if (isPluginEnabled("DecentHolograms")) {
            holograms = new DecentHologramsHandler(plugin);
        } else if (isPluginEnabled("HolographicDisplays")) {
            holograms = new DecentHologramsHandler(plugin);
        } else {
            holograms = new HologramHandler(plugin);
        }
    }

    private static boolean isPluginEnabled(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    public static boolean isEnabled(HookPlugin hookPlugin) {
        return pluginHookMap.containsKey(hookPlugin) && isPluginEnabled(hookPlugin.getPluginName());
    }

    public static boolean isPlayerVanished(Player player) {
        return getVanishHook() != null && getVanishHook().isPlayerVanished(player);
    }

    public static @Nullable VanishHook getVanishHook() {
        if (isEnabled(HookPlugin.PREMIUMVANISH))
            return (PremiumVanishHook) getHook(HookPlugin.PREMIUMVANISH);
        else if (isEnabled(HookPlugin.SUPERVANISH))
            return (SuperVanishHook) getHook(HookPlugin.SUPERVANISH);
        else if (isEnabled(HookPlugin.CMI))
            return (CMIHook) getHook(HookPlugin.CMI);
        else if (isEnabled(HookPlugin.ESSENTIALS))
            return (EssentialsHook) getHook(HookPlugin.ESSENTIALS);
        return null;
    }

    public static @Nullable PermissionHook getPermissionHook() {
        if (isEnabled(HookPlugin.LUCKPERMS))
            return (LuckPermsHook) getHook(HookPlugin.LUCKPERMS);
        return (VaultHook) getHook(HookPlugin.VAULT);
    }
}
