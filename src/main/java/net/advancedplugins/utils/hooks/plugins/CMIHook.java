package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import net.advancedplugins.utils.hooks.VanishHook;
import org.bukkit.entity.Player;

public class CMIHook extends PluginHookInstance implements VanishHook {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.CMI.getPluginName();
    }

    public boolean isPlayerVanished(Player player) {
        return com.Zrips.CMI.CMI.getInstance().getPlayerManager().getUser(player).isVanished();
    }

}
