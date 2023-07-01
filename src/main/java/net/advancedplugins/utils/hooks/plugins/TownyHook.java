package net.advancedplugins.utils.hooks.plugins;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.GRIEFPREVENTION.getPluginName();
    }

    public boolean canBuild(Player p, Location l) {
        return PlayerCacheUtil.getCachePermission(p, l, l.getBlock().getType(), TownyPermission.ActionType.BUILD);
    }

}
