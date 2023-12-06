package net.advancedplugins.utils.pdc;

import net.advancedplugins.utils.ASManager;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public class PDCHandler {

    public static String getString(PersistentDataHolder holder, String key) {
        if(!has(holder, key, PersistentDataType.STRING))
            return null;
        return get(holder, key, PersistentDataType.STRING).toString();
    }

    public static int getInt(PersistentDataHolder holder, String key) {
        if(!has(holder, key, PersistentDataType.INTEGER))
            return 0;
        return (int) get(holder, key, PersistentDataType.INTEGER);
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
        holder.getPersistentDataContainer().set(getNamespace(key), type, value);
    }

    public static void remove(PersistentDataHolder holder, String key) {
        holder.getPersistentDataContainer().remove(getNamespace(key));
    }

    private static Object get(PersistentDataHolder holder, String key, PersistentDataType type) {
        return holder.getPersistentDataContainer().get(getNamespace(key), type);
    }

    public static boolean has(PersistentDataHolder holder, String key, PersistentDataType type) {
        return holder.getPersistentDataContainer().has(getNamespace(key), type);
    }

    private static NamespacedKey getNamespace(String key) {
        return new NamespacedKey(ASManager.getInstance(), key);
    }

    public static void setString(PersistentDataHolder holder, String key, String name) {
        set(holder, key, PersistentDataType.STRING, name);
    }

    public static long getLong(World world, String key) {
        return (long) get(world, key, PersistentDataType.LONG);
    }
}
