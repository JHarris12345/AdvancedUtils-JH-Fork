package net.advancedplugins.utils.hooks.plugins;

import com.viaversion.viaversion.api.Via;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Player;

public class ViaVersionHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.VIAVERSION.getPluginName();
    }

    /* Returns player's version, https://wiki.vg/Protocol_version_numbers */
    public String getPlayerVersion(Player player) {
        return Via.getAPI().getPlayerVersion(player)+"";
    }
}
