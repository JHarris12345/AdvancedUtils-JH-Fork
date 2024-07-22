package net.advancedplugins.utils.hooks.plugins;

import de.myzelyam.api.vanish.VanishAPI;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import net.advancedplugins.utils.hooks.VanishHook;
import org.bukkit.entity.Player;

public class SuperVanishHook extends PluginHookInstance implements VanishHook {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.SUPERVANISH.getPluginName();
    }


    @Override
    public boolean isPlayerVanished(Player player) {
        return VanishAPI.isInvisible(player);
    }
}
