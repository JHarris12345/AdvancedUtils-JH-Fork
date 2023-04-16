package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.pets.api.APAPI;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class APetsHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.ADVANCEDPETS.getPluginName();
    }

    public int getPetLevel(Player p) {
        return APAPI.getPetLevel(p);
    }

}
