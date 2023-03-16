package net.advancedplugins.utils.nbt;

import net.advancedplugins.utils.nbt.backend.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NBTapi {

    public static String getEntity(ItemStack spawner) {
        return get("spawnerType", spawner);
    }

    public static ItemStack addNBTTag(String type, String arguments, ItemStack i) {
        NBTItem nbti = new NBTItem(i);
        nbti.setString(type, arguments);
        return nbti.getItem();
    }

    public static ItemStack addNBTTag(String type, int arguments, ItemStack i) {
        NBTItem nbti = new NBTItem(i);
        nbti.setInteger(type, arguments);
        return nbti.getItem();
    }

    public static boolean contains(String type, ItemStack i) {
        if (i == null || i.getType().equals(Material.AIR))
            return false;

        try {
            NBTItem nbti = new NBTItem(i);
            return nbti.hasKey(type);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public static String get(String argument, ItemStack i) {
        NBTItem nbti = new NBTItem(i);
        return nbti.getString(argument);
    }

    public static boolean hasNBT(ItemStack i) {
        if (i == null) {
            return false;
        }
        if (i.getType().equals(Material.AIR)) {
            return false;
        }
        NBTItem nbti = new NBTItem(i);
        if (nbti.getKeys().size() == 0) {
            return false;
        }
        return true;
    }


    public static ItemStack removeTag(String key, ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        nbti.removeKey(key);
        return nbti.getItem();
    }
}