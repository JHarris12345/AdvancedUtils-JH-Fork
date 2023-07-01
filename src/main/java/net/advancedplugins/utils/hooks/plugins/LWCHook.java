package net.advancedplugins.utils.hooks.plugins;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LWCHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.LWC.getPluginName();
    }

    public boolean canBuild(Player p, Location l) {
        Protection protection = LWC.getInstance().findProtection(l);
        if (protection != null) {
            return LWC.getInstance().canAccessProtection(p, l.getBlock());
        }

        return true;
    }

}
