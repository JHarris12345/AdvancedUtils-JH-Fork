package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import thito.beaconplus.BeaconAPI;


public class BeaconsPlus3Hook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.BEACONPLUS3.getPluginName();
    }

    public boolean isBeaconPlus(Location location) {
        return BeaconAPI.getAPI().getBeaconData(location) != null;
    }
}
