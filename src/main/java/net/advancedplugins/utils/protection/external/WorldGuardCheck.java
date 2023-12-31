package net.advancedplugins.utils.protection.external;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import net.advancedplugins.utils.hooks.plugins.WorldGuardHook;
import net.advancedplugins.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardCheck implements ProtectionType {
    @Override
    public String getName() {
        return "WorldGuard";
    }

    @Override
    public boolean canBreak(Player p, Location b) {
        PluginHookInstance hook = HooksHandler.getHook(HookPlugin.WORLDGUARD);
        if (!hook.isEnabled())
            return true;
        return ((WorldGuardHook) hook).canBuild(p, b);
    }

    @Override
    public boolean canAttack(Player p, Player p2) {
        return true;
    }

    @Override
    public boolean isProtected(Location loc) {
        PluginHookInstance hook = HooksHandler.getHook(HookPlugin.WORLDGUARD);
        if (!hook.isEnabled())
            return false;
        return ((WorldGuardHook)hook).isProtected(loc);
    }
}
