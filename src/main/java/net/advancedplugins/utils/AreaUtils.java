package net.advancedplugins.utils;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.hooks.factions.FactionsPluginHook;
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
            EntityType.ITEM_FRAME, EntityType.PAINTING, EntityType.LEASH_KNOT, EntityType.MINECART,
            EntityType.CHEST_MINECART, EntityType.COMMAND_BLOCK_MINECART, EntityType.FURNACE_MINECART, EntityType.HOPPER_MINECART,
            EntityType.SPAWNER_MINECART, EntityType.TNT_MINECART, EntityType.BOAT,
            EntityType.ITEM, EntityType.ARROW, EntityType.SPECTRAL_ARROW, EntityType.EGG, EntityType.ENDER_PEARL
            , EntityType.EXPERIENCE_ORB, EntityType.FIREBALL, EntityType.FIREWORK_ROCKET,
            EntityType.SMALL_FIREBALL, EntityType.LLAMA_SPIT, EntityType.SNOWBALL, EntityType.POTION,
            EntityType.EXPERIENCE_BOTTLE, EntityType.WITHER_SKULL, EntityType.SHULKER_BULLET, EntityType.TNT,
            EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.LIGHTNING_BOLT, EntityType.AREA_EFFECT_CLOUD, EntityType.UNKNOWN));

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
                    EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(p, target, EntityDamageEvent.DamageCause.CUSTOM,0);
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
                    if (entity instanceof LivingEntity && isDamageable(p, entity)
                            && !ignoredEntities.contains(entity.getType()) && !(entity instanceof Player)) {
                        playersList.add((LivingEntity) entity);
                    }
                }
                break;
            case DAMAGEABLE:
                for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity && isDamageable(p, entity) && !ignoredEntities.contains(entity.getType())) {
                        playersList.add((LivingEntity) entity);
                    }
                }
                break;
            case UNDAMAGEABLE:
                for (Entity entity : p.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity && !isDamageable(p, entity) && !ignoredEntities.contains(entity.getType())) {
                        playersList.add((LivingEntity) entity);
                    }
                }
                break;
        }

        return playersList;
    }

    private static boolean isDamageable(Entity initiator, Entity entity) {
        entity.setMetadata("ae_ignore", new FixedMetadataValue(ASManager.getInstance(), true));
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(entity, entity, EntityDamageEvent.DamageCause.CUSTOM,0);
        Bukkit.getPluginManager().callEvent(event);
        entity.removeMetadata("ae_ignore", ASManager.getInstance());

        if (entity instanceof Player && initiator instanceof Player) {
            if (HooksHandler.isEnabled(HookPlugin.FACTIONS)) {
                FactionsPluginHook factionsHook = ((FactionsPluginHook) HooksHandler.getHook(HookPlugin.FACTIONS));
                String rel = factionsHook.getRelation(((Player) entity), ((Player) initiator));
                if (rel.equals("member")) return false;
            }
        }
        return !event.isCancelled();
    }

    public enum RadiusTarget {
        ALL, PLAYERS, MOBS, DAMAGEABLE, UNDAMAGEABLE
    }
}
