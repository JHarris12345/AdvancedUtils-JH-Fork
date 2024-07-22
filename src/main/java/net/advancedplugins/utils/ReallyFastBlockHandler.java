package net.advancedplugins.utils;

import net.advancedplugins.utils.nbt.backend.ClassWrapper;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReallyFastBlockHandler {

    private static final Map<org.bukkit.World, ReallyFastBlockHandler> handlers = new HashMap<>();

    /**
     * Gets the ReallyFastBlockHandler for the specified
     * world, or creates a new one if none are present.
     */
    public static ReallyFastBlockHandler getForWorld(org.bukkit.World world) {
        if (handlers.containsKey(world)) {
            return handlers.get(world);
        } else {
            ReallyFastBlockHandler rfbh = new ReallyFastBlockHandler(world);
            handlers.put(world, rfbh);
            return rfbh;
        }
    }

    private static Class<?> craftWorldClass;
    private static Class<?> craftBlockData;
    private static Class<?> craftMagicNumbersClass;
    private static Constructor<?> blockPos;
    private static Method getHandle;
    private static Method setType;
    private static Method getState;
    private static Method getMagicBlock;
    private static Method getMagicBlockData;

    static {
        try {
            Class<?> nmsWorldClass = ClassWrapper.NMS_WORLD.getClazz();
            craftWorldClass = ClassWrapper.CRAFT_World.getClazz();
            Class<?> nmsBlockPosition = ClassWrapper.NMS_BLOCKPOSITION.getClazz();
            Class<?> iBlockData = ClassWrapper.NMS_IBLOCKDATA.getClazz();
            if (MinecraftVersion.isNew()) {
                craftBlockData = ClassWrapper.CRAFT_BlockData.getClazz();
                getState = craftBlockData.getMethod("getState");
            }
            blockPos = nmsBlockPosition.getConstructor(int.class, int.class, int.class);
            getHandle = craftWorldClass.getMethod("getHandle");
            setType = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_18_R1) ?
                    nmsWorldClass.getMethod("a", nmsBlockPosition, iBlockData, int.class)
                    : nmsWorldClass.getMethod("setTypeAndData", nmsBlockPosition, iBlockData, int.class);
            if (!MinecraftVersion.isNew()) {
                craftMagicNumbersClass = ClassWrapper.CRAFT_MagicNumbers.getClazz();
                getMagicBlock = craftMagicNumbersClass.getMethod("getBlock", Material.class);
                Class<?> block = ClassWrapper.NMS_Block.getClazz();
                getMagicBlockData = block.getMethod("getBlockData");
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Object nmsWorld;

    public ReallyFastBlockHandler(org.bukkit.World world) {
        Object craftWorld = craftWorldClass.cast(world);
        try {
            nmsWorld = getHandle.invoke(craftWorld);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets an array of blocks to a different material.
     *
     * @param material Material to set blocks to.
     * @param blocks   Blocks to set.
     */
    public void setType(Material material, Block... blocks) {
        if (!Bukkit.isPrimaryThread()) {
            FoliaScheduler.runTask(ASManager.getInstance(), () -> this.setType(material, blocks));
            return;
        }
        try {
            Object ibd = (MinecraftVersion.isNew())
                    ? getState.invoke(craftBlockData.cast(material.createBlockData()))
                    : getMagicBlockData.invoke(getMagicBlock.invoke(craftMagicNumbersClass, material));
            for (Block block : blocks) {
                if (block.getType() == material)
                    continue;
                Object bp = blockPos.newInstance(block.getX(), block.getY(), block.getZ());
                setType.invoke(nmsWorld, bp, ibd, 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
