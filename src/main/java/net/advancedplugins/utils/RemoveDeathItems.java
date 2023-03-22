package net.advancedplugins.utils;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RemoveDeathItems implements Listener {

    private static final HashMap<UUID, List<ItemStack>> itemsCache = new HashMap<>();

    public static void add(UUID uuid, ItemStack item) {
        if (!ASManager.isValid(item)) return;
        List<ItemStack> list = (itemsCache.containsKey(uuid)) ? itemsCache.get(uuid) : new ArrayList<>();
        list.add(item);
        itemsCache.put(uuid, list);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {

        if (e.getEntityType() != EntityType.PLAYER)
            return;

        if (!itemsCache.containsKey(e.getEntity().getUniqueId())) {
            return;
        }

        List<ItemStack> item = itemsCache.remove(e.getEntity().getUniqueId());

        for (ItemStack drop : new ArrayList<>(e.getDrops())) {
            ItemStack dropClone = drop.clone();
            for (ItemStack i : item) {
                ItemStack iClone = i.clone();

                dropClone.setDurability((short) 0);
                iClone.setDurability((short) 0);

                boolean areEqual = dropClone.equals(iClone);
                if (areEqual)
                    e.getDrops().remove(drop);
            }
        }
    }
}
