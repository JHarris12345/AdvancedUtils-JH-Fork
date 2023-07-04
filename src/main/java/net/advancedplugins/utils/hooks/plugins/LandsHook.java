package net.advancedplugins.utils.hooks.plugins;

import com.sk89q.worldguard.protection.flags.Flags;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.enums.FlagTarget;
import me.angeschossen.lands.api.flags.enums.RoleFlagCategory;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import me.angeschossen.lands.api.player.LandPlayer;
import net.advancedplugins.ae.Core;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LandsHook extends PluginHookInstance {

    LandsIntegration landsIntegration = LandsIntegration.of(Core.getInstance());

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.LANDS.getPluginName();
    }

    public boolean canBuild(@NotNull Player player, Location location) {
        LandWorld world = landsIntegration.getWorld(player.getWorld());
        LandPlayer landPlayer = landsIntegration.getLandPlayer(player.getUniqueId());
        if (world == null) return false;
        if (landPlayer == null) return false;
        return world.hasRoleFlag(landPlayer.getUID(), location, RoleFlag.of(landsIntegration, FlagTarget.PLAYER, RoleFlagCategory.ACTION, "build"));
    }

    public boolean canAttack(Player attacker, Player defender){
        return landsIntegration.canPvP(attacker, defender, defender.getLocation(), false, false);
    }
}
