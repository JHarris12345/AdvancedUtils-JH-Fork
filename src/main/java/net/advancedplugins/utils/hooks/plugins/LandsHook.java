package net.advancedplugins.utils.hooks.plugins;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.LandWorld;
import me.angeschossen.lands.api.player.LandPlayer;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LandsHook extends PluginHookInstance {

    LandsIntegration landsIntegration = LandsIntegration.of(ASManager.getInstance());

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.LANDS.getPluginName();
    }

    public boolean canBuild(Player player, Location location) {
        LandWorld world = landsIntegration.getWorld(player.getWorld());
        LandPlayer landPlayer = landsIntegration.getLandPlayer(player.getUniqueId());
        if (world == null) return true;
        if (landPlayer == null) return true;
        return world.hasFlag(player, location, player.getInventory().getItemInMainHand().getType(), Flags.BLOCK_BREAK, false);
    }

    public boolean canAttack(Player attacker, Player defender) {
        return landsIntegration.canPvP(attacker, defender, defender.getLocation(), false, false);
    }

    public boolean canMobsSpawn(Location loc) {
        try {
            LandWorld world = landsIntegration.getWorld(loc.getWorld());
            if (world == null) return true;
            Area area = world.getArea(loc);
            if (area == null) return true;
            return area.hasNaturalFlag(Flags.MONSTER_SPAWN);
        } catch (Exception ev) {
            ev.printStackTrace();
            return true;
        }
    }

    public boolean isProtected(Location loc) {
        return landsIntegration.getArea(loc) != null;
    }
}
