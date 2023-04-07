package net.advancedplugins.utils.hooks.plugins;

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
                    com.sk89q.worldguard.protection.flags.Flags.BUILD) || com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testState(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(l),
                    com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(p),
                    com.sk89q.worldguard.protection.flags.Flags.BLOCK_PLACE);
        } catch (Exception ev) {
            ev.printStackTrace();
            ASManager.getInstance().getLogger().warning("Error with WorldGuard v(" + (Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion() + ") - Version unsupported"));
        }
        return false;
    }

}
