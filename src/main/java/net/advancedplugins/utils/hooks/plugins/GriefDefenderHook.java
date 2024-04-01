package net.advancedplugins.utils.hooks.plugins;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustTypes;
import com.griefdefender.api.permission.flag.Flag;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class GriefDefenderHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.GRIEFDEFENDER.getPluginName();
    }

    public boolean canBuild(Player p, Location l) {
        final Claim claim = GriefDefender.getCore().getClaimAt(l);
        if(claim == null) return true;
        // todo finish
        return true;
    }

    public boolean canIceGenerate(Location l) {
        try {
            final Claim claim = GriefDefender.getCore().getClaimAt(l);
            if (claim == null) return true;
            return claim.getFlagPermissionValue(Flag.builder().name("ice-growth").build(), new HashSet<>()).asBoolean();
        } catch (Exception ignored) {
            return true;
        }
    }

}
