package net.advancedplugins.utils.protection.external;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import net.advancedplugins.utils.hooks.plugins.SlimeFunHook;
import net.advancedplugins.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SlimeFunCheck implements ProtectionType {
    @Override
    public String getName() {
        return "SlimeFun";
    }

    @Override
    public boolean canBreak(Player p, Location b) {
        PluginHookInstance hook = HooksHandler.getHook(HookPlugin.SLIMEFUN);
        if (!hook.isEnabled())
            return true;
        return ((SlimeFunHook) hook).canBuild(p, b);
    }

    @Override
    public boolean canAttack(Player p, Player p2) {
        return true;
    }

    @Override
    public boolean isProtected(Location loc) {
        return false;
    }
}
