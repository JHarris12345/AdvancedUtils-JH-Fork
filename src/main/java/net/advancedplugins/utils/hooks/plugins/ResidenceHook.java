package net.advancedplugins.utils.hooks.plugins;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ResidenceHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.RESIDENCE.getPluginName();
    }

    public boolean canBuild(Player p, Location l) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(l);
        if (res == null)
            return true;

        ResidencePermissions perms = res.getPermissions();
        return perms.playerHas(p, Flags.build, true);
    }

    public boolean isProtected(Location l) {
        ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(l);
        return res != null;
    }

}
