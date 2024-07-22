package net.advancedplugins.utils;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.hooks.factions.FactionsPluginHook;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AreaUtils {

    private static final List<String> ignoredEntityNamesPre1_20_6 = Arrays.asList(
            "ARMOR_STAND", "ITEM_FRAME", "PAINTING", "LEASH_HITCH", "MINECART", "MINECART_CHEST", "MINECART_COMMAND",
            "MINECART_FURNACE", "MINECART_HOPPER", "MINECART_MOB_SPAWNER", "MINECART_TNT", "BOAT", "FISHING_HOOK",
            "DROPPED_ITEM", "ARROW", "SPECTRAL_ARROW", "EGG", "ENDER_PEARL", "ENDER_SIGNAL", "EXPERIENCE_ORB",
            "FIREBALL", "FIREWORK", "SMALL_FIREBALL", "LLAMA_SPIT", "SNOWBALL", "SPLASH_POTION", "THROWN_EXP_BOTTLE",
            "WITHER_SKULL", "SHULKER_BULLET", "PRIMED_TNT", "TRIDENT", "DRAGON_FIREBALL", "LIGHTNING", "AREA_EFFECT_CLOUD", "UNKNOWN"
    );
    private static final List<String> ignoredEntityNames1_20_6 = Arrays.asList(
            "ARMOR_STAND", "ITEM_FRAME", "PAINTING", "LEASH_KNOT", "MINECART",
            "CHEST_MINECART", "COMMAND_BLOCK_MINECART", "FURNACE_MINECART", "HOPPER_MINECART",
            "SPAWNER_MINECART", "TNT_MINECART", "BOAT", "ITEM", "ARROW", "SPECTRAL_ARROW",
            "EGG", "ENDER_PEARL", "EXPERIENCE_ORB", "FIREBALL", "FIREWORK_ROCKET",
            "SMALL_FIREBALL", "LLAMA_SPIT", "SNOWBALL", "POTION", "EXPERIENCE_BOTTLE",
            "WITHER_SKULL", "SHULKER_BULLET", "TNT", "TRIDENT", "DRAGON_FIREBALL",
            "LIGHTNING_BOLT", "AREA_EFFECT_CLOUD", "UNKNOWN"
    );

    private static final List<EntityType> ignoredEntities = Collections.unmodifiableList(
            (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ? ignoredEntityNames1_20_6 : ignoredEntityNamesPre1_20_6)
                    .stream()
                    .map(EntityType::fromName)
                    .collect(Collectors.toList())
    );

    // done for AE version 3.0.0 release (to be concluded)
    // OLD IMPLEMENTATION
    @SuppressWarnings("removal")
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

    @SuppressWarnings("removal")
    private static boolean isDamageable(Entity initiator, Entity entity) {
        entity.setMetadata("ae_ignore", new FixedMetadataValue(ASManager.getInstance(), true));
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(entity, entity, EntityDamageEvent.DamageCause.CUSTOM, 0);
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
