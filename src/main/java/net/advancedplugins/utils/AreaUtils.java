package net.advancedplugins.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AreaUtils {

    private static final List<EntityType> ignoredEntities = Collections.unmodifiableList(Arrays.asList(EntityType.ARMOR_STAND,
            EntityType.ITEM_FRAME, EntityType.PAINTING, EntityType.LEASH_HITCH, EntityType.MINECART,
            EntityType.MINECART_CHEST, EntityType.MINECART_COMMAND, EntityType.MINECART_FURNACE, EntityType.MINECART_HOPPER,
            EntityType.MINECART_MOB_SPAWNER, EntityType.MINECART_TNT, EntityType.BOAT, EntityType.FISHING_HOOK,
            EntityType.DROPPED_ITEM, EntityType.ARROW, EntityType.SPECTRAL_ARROW, EntityType.EGG, EntityType.ENDER_PEARL,
            EntityType.ENDER_SIGNAL, EntityType.EXPERIENCE_ORB, EntityType.FIREBALL, EntityType.FIREWORK,
            EntityType.SMALL_FIREBALL, EntityType.LLAMA_SPIT, EntityType.SNOWBALL, EntityType.SPLASH_POTION,
            EntityType.THROWN_EXP_BOTTLE, EntityType.WITHER_SKULL, EntityType.SHULKER_BULLET, EntityType.PRIMED_TNT,
            EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.LIGHTNING, EntityType.AREA_EFFECT_CLOUD, EntityType.UNKNOWN));

    // done for AE version 3.0.0 release (to be concluded)
    // OLD IMPLEMENTATION
    public static List<LivingEntity> getEntitiesInRadius(int radius, Entity p, boolean checkForDamage, boolean damageable, boolean mobs) {
        List<LivingEntity> playersList = new ArrayList<>();

        // Check for players only.
        if (!mobs) {
            for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                if (!(entity instanceof Player)) continue;
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
                if (!(ent instanceof LivingEntity)) continue;
                if (ent instanceof Player) continue;
                if (ent instanceof ArmorStand) continue;
                playersList.add((LivingEntity) ent);
            }
        }
        return playersList;
    }

    public static List<LivingEntity> getEntitiesInRadius(int radius, Entity p, RadiusTarget target) {
        List<LivingEntity> playersList = new ArrayList<>();

        switch (target) {
            case ALL:
                for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity && !ignoredEntities.contains(entity.getType())) {
                        playersList.add((LivingEntity) entity);
                    }
                }
                break;
            case PLAYERS:
                for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof Player) {
                        playersList.add((Player) entity);
                    }
                }
                break;
            case MOBS:
                for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity && isDamageable(entity)
                            && !ignoredEntities.contains(entity.getType()) && !(entity instanceof Player)) {
                        playersList.add((LivingEntity) entity);
                    }
                }
                break;
            case DAMAGEABLE:
                for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity && isDamageable(entity) && !ignoredEntities.contains(entity.getType())) {
                        playersList.add((LivingEntity) entity);
                    }
                }
                break;
            case UNDAMAGEABLE:
                for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity && !isDamageable(entity) && !ignoredEntities.contains(entity.getType())) {
                        playersList.add((LivingEntity) entity);
                    }
                }
                break;
        }

        return playersList;
    }

    private static boolean isDamageable(Entity entity) {
        entity.setMetadata("ae_ignore", new FixedMetadataValue(ASManager.getInstance(), true));
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(entity, entity, EntityDamageEvent.DamageCause.CUSTOM, 0);
        Bukkit.getPluginManager().callEvent(event);
        entity.removeMetadata("ae_ignore", ASManager.getInstance());
        return !event.isCancelled();
    }

    public enum RadiusTarget {
        ALL, PLAYERS, MOBS, DAMAGEABLE, UNDAMAGEABLE
    }
}
