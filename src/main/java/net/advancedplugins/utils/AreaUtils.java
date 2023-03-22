package net.advancedplugins.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class AreaUtils {

    // AOE accessibility application controller
    // done for AE version 3.0.0 release (to be concluded)
    public static List<LivingEntity> getPlayersInRadius(int radius, Entity p, boolean checkForDamage, boolean damageable, boolean mobs) {
        List<LivingEntity> playersList = new ArrayList<>();

        // Check for players only.
        if (!mobs) {
            for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                if (!(entity instanceof Player))
                    continue;
                Player target = (Player) entity;
                if (checkForDamage) {
                    target.setMetadata("ae_ignore", new FixedMetadataValue(ASManager.getInstance(), true));
                    EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(p, target, EntityDamageEvent.DamageCause.CUSTOM, 0);
                    Bukkit.getPluginManager().callEvent(event);
                    target.removeMetadata("ae_ignore", ASManager.getInstance());
                    if (event.isCancelled()) {
                        if (damageable) {
                            continue;
                        }
                    } else {
                        if (!damageable) {
                            continue;
                        }
                    }
                }

                playersList.add(target);
            }

            // Check for mobs only.
        } else {
            for (Entity ent : p.getNearbyEntities(radius, radius, radius)) {
                if (!(ent instanceof LivingEntity))
                    continue;
                if (ent instanceof Player)
                    continue;
                if (ent instanceof ArmorStand)
                    continue;
                playersList.add((LivingEntity) ent);
            }
        }
        return playersList;
    }

}
