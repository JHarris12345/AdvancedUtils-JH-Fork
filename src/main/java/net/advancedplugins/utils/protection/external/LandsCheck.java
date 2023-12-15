package net.advancedplugins.utils.protection.external;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.hooks.plugins.LandsHook;
import net.advancedplugins.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LandsCheck implements ProtectionType {
    @Override
    public String getName() {
        return "Lands";
    }

    @Override
    public boolean canBreak(Player p, Location b) {
        LandsHook hook = (LandsHook) HooksHandler.getHook(HookPlugin.LANDS);

        if (hook == null) {
            return true;
        }

        return hook.canBuild(p, b);
    }

    @Override
    public boolean canAttack(Player p, Player p2) {
        LandsHook hook = (LandsHook) HooksHandler.getHook(HookPlugin.LANDS);

        if (hook == null) {
            return true;
        }

        return hook.canAttack(p, p2);
    }

    @Override
    public boolean isProtected(Location loc) {
        return false;
    }
}
