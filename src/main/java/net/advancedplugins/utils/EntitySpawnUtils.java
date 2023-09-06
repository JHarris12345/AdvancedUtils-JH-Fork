package net.advancedplugins.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class EntitySpawnUtils implements Listener {
    public static String metaDataPrefix = "advanced";
    private final Plugin plugin;

    public EntitySpawnUtils(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawns an entity and adds a metadata value to it to allow other
     * plugins to detect these mobs
     *
     * @param plugin Plugin instance
     * @param world World
     * @param location Location to spawn the entity at
     * @param entityType Type of the entity
     * @return Entity spawned
     * @implNote <p><b>IT IS HIGHLY SUGGESTED TO REGISTER THIS CLASS AS EVENT LISTENER TO REMOVE THE METADATA ON THE ENTITY DEATH TO PREVENT MEMORY LEAKS</b></p>
     */
    public static Entity spawnEntity(@NotNull Plugin plugin, @NotNull World world, @NotNull Location location, @NotNull EntityType entityType) {
        Entity entity = world.spawnEntity(location, entityType);
        entity.setMetadata(metaDataPrefix + "-entity", new FixedMetadataValue(plugin, true));
        return entity;
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event) {
        event.getEntity().removeMetadata(metaDataPrefix + "-entity", plugin);
    }
}
