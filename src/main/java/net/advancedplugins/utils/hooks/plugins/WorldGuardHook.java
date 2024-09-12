package net.advancedplugins.utils.hooks.plugins;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "WorldGuardHook";
    }

    public boolean canBuild(Player p, Location l) {
        if (p.isOp())
            return true;
        try {
            return com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testState(
                    com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(l),
                    com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(p),
                    Flags.BUILD) ||  com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testState(
                    com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(l),
                    com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(p),
                    Flags.BLOCK_BREAK);
        } catch (Exception ev) {
            ev.printStackTrace();
            ASManager.getInstance().getLogger().warning("Error with WorldGuard v(" + (Bukkit.getPluginManager().getPlugin("WorldGuard")
                    .getDescription().getVersion() + ") - Version unsupported"));
        }
        return false;
    }

    public boolean isProtected(Location loc) {
        try {
            return com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer()
                    .get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(loc.getWorld()))
                    .getApplicableRegions(com.sk89q.worldedit.bukkit.BukkitAdapter.asBlockVector(loc)).size() > 0;
        } catch (Exception ev) {
            return false;
        }
    }

    public boolean canMobsSpawn(Location location) {
        for (ProtectedRegion applicableRegion : WorldGuard.getInstance().getPlatform().getRegionContainer()
            .get(BukkitAdapter.adapt(location.getWorld()))
            .getApplicableRegions(BukkitAdapter.asBlockVector(location))) {

            if (applicableRegion.getFlag(Flags.MOB_SPAWNING) == StateFlag.State.DENY) {
                return false;
            }
        }

        return true;
    }

}
