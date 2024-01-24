package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import us.lynuxcraft.deadsilenceiv.advancedchests.AdvancedChestsPlugin;


// this hook is technically not used anywhere, but if it is needed it's here haha
public class AdvancedChestsHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.ADVANCEDCHESTS.getPluginName();
    }

    public boolean isAdvancedChest(Location location) {
        return AdvancedChestsPlugin.getInstance().getChestsManager().getAdvancedChest(location) != null;
    }
}
