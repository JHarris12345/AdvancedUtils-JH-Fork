package net.advancedplugins.utils.hooks.plugins;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.entity.LivingEntity;

public class MythicMobsHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.MYTHICMOBS.getPluginName();
    }

    public boolean isMythicMob(LivingEntity ent) {
        return MythicBukkit.inst().getAPIHelper().isMythicMob(ent.getUniqueId());
    }

}
