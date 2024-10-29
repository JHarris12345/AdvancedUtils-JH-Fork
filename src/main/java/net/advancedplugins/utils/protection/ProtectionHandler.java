package net.advancedplugins.utils.protection;

import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.LocalLocation;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.hooks.plugins.GriefDefenderHook;
import net.advancedplugins.utils.protection.external.*;
import net.advancedplugins.utils.protection.internal.GlobalProtCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ProtectionHandler {

    private final HashMap<String, ProtectionType> protectionMap = new HashMap<>();

    public ProtectionHandler(JavaPlugin plugin) {
        if (HooksHandler.isEnabled(HookPlugin.WORLDGUARD))
            register(plugin, new WorldGuardCheck());
        if (HooksHandler.isEnabled(HookPlugin.SLIMEFUN))
            register(plugin, new SlimeFunCheck());

        // Factions plugins hook
        if (HooksHandler.isEnabled(HookPlugin.FACTIONS))
            register(plugin, new FactionsCheck());

        // Lands
        if (HooksHandler.isEnabled(HookPlugin.LANDS))
            register(plugin, new LandsCheck());

        if (HooksHandler.isEnabled(HookPlugin.PROTECTIONSTONES))
            register(plugin, new ProtectionStonesCheck());

        if (HooksHandler.isEnabled(HookPlugin.GRIEFDEFENDER))
            register(plugin, new GriefDefenderCheck());

//        if(HooksHandler.isEnabled(HookPlugin.GRIEFPREVENTION))
//            register(plugin, new );

        // if no protection plugins are found, use this check
//        if (protectionMap.isEmpty())
        register(plugin, new GlobalProtCheck());
    }

    public void register(JavaPlugin plugin, ProtectionType prot) {
        if (!plugin.equals(ASManager.getInstance()))
            ASManager.getInstance().getLogger().info(plugin.getName() + " register a new protection check: " + prot.getName());
        this.protectionMap.put(prot.getName(), prot);
    }

    public boolean canBreak(Location b, Player p) {
        boolean canBreak = protectionMap.values().stream().allMatch(prot -> prot.canBreak(p, b));
        ASManager.debug("[LAND PROT] Can " + p.getName() + " break at " + new LocalLocation(b).getEncode() + "? " + canBreak);
        return canBreak;
    }

    public boolean canAttack(Player p, Player p2) {
        return protectionMap.values().stream().anyMatch(prot -> !prot.canAttack(p, p2));
    }

    public boolean isProtected(Location location) {
        return protectionMap.values().stream().anyMatch(prot -> prot.isProtected(location));
    }
}
