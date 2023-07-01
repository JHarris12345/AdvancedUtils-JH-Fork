package net.advancedplugins.utils.hooks.plugins;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefPreventionHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.GRIEFPREVENTION.getPluginName();
    }

    public boolean canBuild(Player p, Location l) {
        Claim c = GriefPrevention.instance.dataStore.getClaimAt(l, false, null);
        if (c == null)
            return true;
        return c.allowAccess(p) == null;
    }

}
