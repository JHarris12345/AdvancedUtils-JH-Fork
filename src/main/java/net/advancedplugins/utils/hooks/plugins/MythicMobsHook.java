package net.advancedplugins.utils.hooks.plugins;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import lombok.Getter;
import net.advancedplugins.utils.SchedulerUtils;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class MythicMobsHook extends PluginHookInstance implements Listener {
    @Getter
    private static final Set<Entity> ignoreEnchantsMobs = new HashSet<>();

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

    @EventHandler(priority = EventPriority.LOWEST)
    private void onMythicDamage(MythicDamageEvent event) {
        if (event.getDamageMetadata().getIgnoreEnchantments()) {
            ignoreEnchantsMobs.add(event.getCaster().getEntity().getBukkitEntity());
            /*
             process this first in ExecutionTask and then remove. If removed right in ExecutionTask the check didn't work
             My theory is that one event it plays MythicDamageEvent and other it plays normal Bukkit event, so if we remove it right away
             it won't check correctly for the Bukkit event (was removed before it)
             (Wega)
            */
            SchedulerUtils.runTaskLater(() -> ignoreEnchantsMobs.remove(event.getCaster().getEntity().getBukkitEntity()), 4L);
        }
    }
}
