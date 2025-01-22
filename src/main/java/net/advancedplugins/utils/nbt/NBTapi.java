package net.advancedplugins.utils.nbt;

import net.advancedplugins.utils.pdc.PDCHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class NBTapi {

    public static ItemStack addNBTTag(String type, String arguments, ItemStack i) {
        ItemMeta meta = i.getItemMeta();
        PDCHandler.setString(meta, type, arguments);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack addNBTTag(String type, int arguments, ItemStack i) {
        ItemMeta meta = i.getItemMeta();
        PDCHandler.setInt(meta, type, arguments);
        i.setItemMeta(meta);
        return i;
    }

    public static ItemStack addNBTTag(String type, long arguments, ItemStack i) {
        ItemMeta meta = i.getItemMeta();
        PDCHandler.setLong(meta, type, arguments);
        i.setItemMeta(meta);
        return i;
    }
    public static boolean contains(String type, ItemStack i) {
        if (i == null || i.getType().equals(Material.AIR)) return false;

        // This checks for all possible PDC types if item has it
        if (PDCHandler.contains(i, type)) return true;

//        try {
//            NBTItem nbti = new NBTItem(i);
//            return nbti.hasKey(type);
//        } catch (NullPointerException ex) {
//            return false;
//        }
        return false;
    }

//    public static boolean containsNbt(String type, ItemStack i) {
//        if (i == null || i.getType().equals(Material.AIR)) return false;
//
//        try {
//            NBTItem nbti = new NBTItem(i);
//            return nbti.hasKey(type);
//        } catch (NullPointerException ex) {
//            return false;
//        }
//    }

    public static String get(String argument, ItemStack i) {
        if (i == null || i.getType().isAir()) return null;
        if (i.hasItemMeta() && PDCHandler.hasString(i.getItemMeta(), argument))
            return PDCHandler.getString(i.getItemMeta(), argument);

//        if (!containsNbt(argument, i)) return null;
//
//        String nbtValue = new NBTItem(i).getString(argument);
//        addNBTTag(argument, nbtValue, i);
//        return nbtValue;
        return null;
    }


    public static Integer getInt(String argument, ItemStack i) {
        if (i == null || i.getType().isAir()) return null;
        if (PDCHandler.hasInt(i.getItemMeta(), argument)) return PDCHandler.getInt(i.getItemMeta(), argument);

//        if (!containsNbt(argument, i)) {
//            return 0;
//        }
//
//        Integer nbtValue = new NBTItem(i).getInteger(argument);
//        addNBTTag(argument, nbtValue, i);
//        return nbtValue;
        return 0;
    }

    public static long getLong(String argument, ItemStack i) {
        if (i == null) return 0;
        if (PDCHandler.has(i.getItemMeta(), argument, PersistentDataType.LONG))
            return PDCHandler.getLong(i.getItemMeta(), argument);
//
//        if (!containsNbt(argument, i)) return 0;
//
//        long nbtValue = new NBTItem(i).getLong(argument);
//        ItemMeta meta = i.getItemMeta();
//        PDCHandler.setLong(meta, argument, nbtValue);
//        i.setItemMeta(meta);
//        return nbtValue;
        return 0;
    }

    public static ItemStack removeTag(String key, ItemStack item) {
//        if (containsNbt(key, item)) {
//            NBTItem nbti = new NBTItem(item);
//            nbti.removeKey(key);
//
//            item = nbti.getItem();
//        }

        ItemMeta meta = item.getItemMeta();
        PDCHandler.remove(meta, key);
        item.setItemMeta(meta);
        return item;
    }
}