package net.advancedplugins.utils;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ACooldown {

    private static final Map<UUID, Map<String, Double>> cooldown = new HashMap<>();

    /**
     * Clears the cooldown map.
     */
    public static void reload() {
        cooldown.clear();
    }

    /**
     * Checks if an entity is in cooldown.
     *
     * @param entity Entity to check.
     * @param ae     Enchant to check.
     * @return True if the entity is in cooldown for the provided enchant, false otherwise.
     */
    public static boolean isInCooldown(LivingEntity entity, String ae) {
        if (entity == null || ae == null)
            return false;
        UUID uuid = entity.getUniqueId();
        if (!cooldown.containsKey(uuid))
            return false;

        Map<String, Double> cd = cooldown.get(uuid);
        if (!cd.containsKey(ae))
            return false;

        double next = cd.get(ae);
        if (next >= System.currentTimeMillis())
            return true;

        cd.remove(ae);
        cooldown.put(uuid, cd);
        return false;
    }

    /**
     * Puts a player to cooldown for an enchant.
     *
     * @param entity   Entity to put to cooldown.
     * @param ae       Enchant to add the cooldown for.
     * @param cooldown Duration of the cooldown.
     */
    public static void putToCooldown(LivingEntity entity, String ae, double cooldown) {
        if (entity == null || ae == null)
            return;
        UUID uuid = entity.getUniqueId();
        Map<String, Double> map = ACooldown.cooldown.getOrDefault(uuid, new HashMap<>());
        map.put(ae, ((System.currentTimeMillis()) + (cooldown * 1000)));
        ACooldown.cooldown.put(uuid, map);
    }

}
