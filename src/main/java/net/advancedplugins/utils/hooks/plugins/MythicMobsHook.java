package net.advancedplugins.utils.hooks.plugins;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
        MobManager mobManager = MythicMobs.inst().getMobManager();
        return mobManager.isActiveMob(ent.getUniqueId());
    }

}
