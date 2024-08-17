package net.advancedplugins.utils.pdc;

import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.nbt.NBTapi;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class PDCHandler {

    private static final List<PersistentDataType> dataTypes = Arrays.asList(
            PersistentDataType.BYTE,
            PersistentDataType.BYTE_ARRAY,
            PersistentDataType.DOUBLE,
            PersistentDataType.FLOAT,
            PersistentDataType.INTEGER,
            PersistentDataType.INTEGER_ARRAY,
            PersistentDataType.LONG,
            PersistentDataType.SHORT,
            PersistentDataType.STRING
    );

    public static boolean contains(PersistentDataHolder holder, String key) {
        return dataTypes.stream().filter(type -> holder.getPersistentDataContainer().has(getNamespace(key), type)).findFirst().orElse(null) != null;
    }

    public static String getString(PersistentDataHolder holder, String key) {
        if(!has(holder, key, PersistentDataType.STRING)) {
            if (MinecraftVersion.getVersionNumber() < 1_20_5 && holder instanceof ItemStack && NBTapi.contains(key, ((ItemStack) holder))) {
                String nbtVal = NBTapi.get(key, ((ItemStack) holder));
                PDCHandler.setString(holder, key, nbtVal);
                return nbtVal;
            }
            return null;
        }
        return get(holder, key, PersistentDataType.STRING).toString();
    }

    public static int getInt(PersistentDataHolder holder, String key) {
        if(!has(holder, key, PersistentDataType.INTEGER))
            return 0;
        return (int) get(holder, key, PersistentDataType.INTEGER);
    }

    public static long getLong(PersistentDataHolder holder, String key) {
        if(!has(holder, key, PersistentDataType.LONG))
            return 0;
        return (long) get(holder, key, PersistentDataType.LONG);
    }

    public static double getDouble(PersistentDataHolder holder, String key) {
        if(!has(holder, key, PersistentDataType.DOUBLE))
            return 0.0D;
        return (double) get(holder, key, PersistentDataType.DOUBLE);
    }

    public static boolean getBoolean(PersistentDataHolder holder, String key) {
        if(!has(holder, key, PersistentDataType.BYTE))
            return false;
        return (boolean) get(holder, key, PersistentDataType.BYTE);
    }

    public static void set(PersistentDataHolder holder, String key, PersistentDataType type, Object value) {
        if (holder == null) return;
        holder.getPersistentDataContainer().set(getNamespace(key), type, value);
    }

    public static void remove(PersistentDataHolder holder, String key) {
        if (holder == null) return;
        holder.getPersistentDataContainer().remove(getNamespace(key));
    }

    private static Object get(PersistentDataHolder holder, String key, PersistentDataType type) {
        if (holder instanceof ItemStack && !((ItemStack) holder).hasItemMeta()) return null;
        return holder.getPersistentDataContainer().get(getNamespace(key), type);
    }

    public static boolean has(PersistentDataHolder holder, String key, PersistentDataType type) {
        if (holder instanceof ItemStack && !((ItemStack) holder).hasItemMeta()) return false;
        if (holder == null) return false;

        return holder.getPersistentDataContainer().has(getNamespace(key), type);
    }

    public static boolean hasString(PersistentDataHolder holder, String key) {
        return has(holder, key, PersistentDataType.STRING);
    }

    public static boolean hasInt(PersistentDataHolder holder, String key) {
        return has(holder, key, PersistentDataType.INTEGER);
    }

    public static boolean hasBoolean(PersistentDataHolder holder, String key) {
        return has(holder, key, PersistentDataType.BYTE);
    }

    public static NamespacedKey getNamespace(String key) {
        return new NamespacedKey(ASManager.getInstance(), key.replace(";", "-"));
    }

    public static void setString(PersistentDataHolder holder, String key, String name) {
        set(holder, key, PersistentDataType.STRING, name);
    }

    public static void setBoolean(PersistentDataHolder holder, String key, boolean bool) {
        set(holder, key, PersistentDataType.BYTE, (byte) (bool ? 1 : 0));
    }

    public static void setLong(PersistentDataHolder holder, String key, long l) {
        set(holder, key, PersistentDataType.LONG, l);
    }

    public static void setDouble(PersistentDataHolder holder, String key, double d) {
        set(holder, key, PersistentDataType.DOUBLE, d);
    }

    public static void setInt(PersistentDataHolder holder, String key, int i) {
        set(holder, key, PersistentDataType.INTEGER, i);
    }

    public static long getLong(World world, String key) {
        return (long) get(world, key, PersistentDataType.LONG);
    }

    public static void unset(PersistentDataHolder holder, String chatColor) {
        if (holder == null) return;
        holder.getPersistentDataContainer().remove(getNamespace(chatColor));
    }

    public static boolean hasPDC(PersistentDataHolder holder) {
        if (holder == null) return false;
        return !holder.getPersistentDataContainer().getKeys().isEmpty();
    }

    public static List<String> getKeys(ItemStack i) {
        return i.hasItemMeta() ? i.getItemMeta().getPersistentDataContainer().getKeys()
                .stream().map(NamespacedKey::getKey).collect(Collectors.toList()): Collections.EMPTY_LIST;
    }

    public static Object get(ItemStack itemInHand, NamespacedKey key) {
        for(PersistentDataType type : dataTypes) {
            if (itemInHand.getItemMeta().getPersistentDataContainer().has(key, type)) {
                return itemInHand.getItemMeta().getPersistentDataContainer().get(key, type);
            }
        }
        return null;
    }
}