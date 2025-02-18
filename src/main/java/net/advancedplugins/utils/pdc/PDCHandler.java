package net.advancedplugins.utils.pdc;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.advancedplugins.utils.ASManager;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
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

    public static boolean contains(ItemStack itemStack, String key) {
        return dataTypes.stream().filter(type -> itemStack.getPersistentDataContainer().has(getNamespace(key), type)).findFirst().orElse(null) != null;
    }

    public static String getString(ItemStack itemStack, String key, String def) {
        if(!has(itemStack, key, PersistentDataType.STRING))
            return def;
        return get(itemStack, key, PersistentDataType.STRING).toString();
    }

    public static String getString(ItemStack itemStack, String key) {
        if(!has(itemStack, key, PersistentDataType.STRING)) {
            return null;
        }
        return get(itemStack, key, PersistentDataType.STRING).toString();
    }

    public static int getInt(ItemStack itemStack, String key) {
        if(!has(itemStack, key, PersistentDataType.INTEGER))
            return 0;
        return (int) get(itemStack, key, PersistentDataType.INTEGER);
    }

    public static long getLong(ItemStack itemStack, String key) {
        if(!has(itemStack, key, PersistentDataType.LONG))
            return 0;
        return (long) get(itemStack, key, PersistentDataType.LONG);
    }

    public static float getFloat(ItemStack itemStack, String key) {
        if(!has(itemStack, key, PersistentDataType.FLOAT))
            return 0.0F;
        return (float) get(itemStack, key, PersistentDataType.FLOAT);
    }

    public static float getFloat(Entity entity, String key) {
        if(!has(entity, key, PersistentDataType.FLOAT))
            return 0.0F;
        return (float) get(entity, key, PersistentDataType.FLOAT);
    }

    public static double getDouble(ItemStack itemStack, String key) {
        if(!has(itemStack, key, PersistentDataType.DOUBLE))
            return 0.0D;
        return (double) get(itemStack, key, PersistentDataType.DOUBLE);
    }

    public static boolean getBoolean(ItemStack itemStack, String key) {
        if(!has(itemStack, key, PersistentDataType.BYTE))
            return false;
        return (byte) get(itemStack, key, PersistentDataType.BYTE) == 1;
    }

    public static void set(PersistentDataHolder holder, String key, PersistentDataType type, Object value) {
        if (holder == null) return;
        holder.getPersistentDataContainer().set(getNamespace(key), type, value);
    }

    public static void remove(PersistentDataHolder holder, String key) {
        holder.getPersistentDataContainer().remove(getNamespace(key));
    }

    private static Object get(ItemStack itemStack, String key, PersistentDataType type) {
        return itemStack.getPersistentDataContainer().get(getNamespace(key), type);
    }

    private static Object get(Entity entity, String key, PersistentDataType type) {
        return entity.getPersistentDataContainer().get(getNamespace(key), type);
    }

    public static boolean has(ItemStack itemStack, String key, PersistentDataType type) {
        if (itemStack == null) return false;
        return itemStack.getPersistentDataContainer().has(getNamespace(key), type);
    }

    public static boolean has(Entity entity, String key, PersistentDataType type) {
        if (entity == null) return false;
        return entity.getPersistentDataContainer().has(getNamespace(key), type);
    }

    public static boolean hasString(ItemStack itemStack, String key) {
        return has(itemStack, key, PersistentDataType.STRING);
    }

    public static boolean hasInt(ItemStack itemStack, String key) {
        return has(itemStack, key, PersistentDataType.INTEGER);
    }

    public static boolean hasBoolean(ItemStack itemStack, String key) {
        return has(itemStack, key, PersistentDataType.BYTE);
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

    /*public static long getLong(World world, String key) {
        return (long) get(world, key, PersistentDataType.LONG);
    }*/

    public static void unset(PersistentDataHolder holder, String chatColor) {
        if (holder == null) return;
        holder.getPersistentDataContainer().remove(getNamespace(chatColor));
    }

    public static boolean hasPDC(PersistentDataHolder holder) {
        if (holder == null) return false;
        return !holder.getPersistentDataContainer().getKeys().isEmpty();
    }

    /**
     * Get all keys from an item
     * @param i ItemStack
     * @return List of keys
     */
    public static List<String> getKeys(ItemStack i) {
        if (i == null) {
            return Collections.emptyList();
        }

        PersistentDataContainerView container = i.getPersistentDataContainer();
        Set<NamespacedKey> keySet = container.getKeys();

        if (keySet.isEmpty()) {
            return Collections.emptyList();
        }

        if (keySet.size() == 1) {
            return Collections.singletonList(keySet.iterator().next().getKey());
        }

        List<String> result = new ArrayList<>(keySet.size());
        for (NamespacedKey key : keySet) {
            result.add(key.getKey());
        }
        return Collections.unmodifiableList(result);
    }

    public static Object get(ItemStack itemInHand, NamespacedKey key) {
        for(PersistentDataType type : dataTypes) {
            if (itemInHand.getItemMeta().getPersistentDataContainer().has(key, type)) {
                return itemInHand.getItemMeta().getPersistentDataContainer().get(key, type);
            }
        }
        return null;
    }

    public static void set(Block block, String key, PersistentDataType type, String value) {
        block.getChunk().getPersistentDataContainer().set(getNamespace(blockToString(block)+key), type, value);
    }

    public static Object get(Block block, String key) {
        for(PersistentDataType type : dataTypes) {
            if (block.getChunk().getPersistentDataContainer().has(getNamespace(blockToString(block)+key), type)) {
                return block.getChunk().getPersistentDataContainer().get(getNamespace(blockToString(block)+key), type);
            }
        }
        return null;
    }

    public static void remove(Block block, String key) {
        block.getChunk().getPersistentDataContainer().remove(getNamespace(blockToString(block)+key));
    }

    public static boolean has(Block block, String key) {
        for(PersistentDataType type : dataTypes) {
            if (block.getChunk().getPersistentDataContainer().has(getNamespace(blockToString(block)+key), type)) {
                return true;
            }
        }
        return false;
    }

    private static String blockToString(Block block) {
        return block.getX() + ";" + block.getY() + ";" + block.getZ()+";";
    }

    public static String getKeys(Player player) {
        return player.getPersistentDataContainer().getKeys().stream().map(NamespacedKey::getKey).collect(Collectors.joining(","));
    }
}