package net.advancedplugins.utils.nbt.utils;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiConsumer;

public interface ReadWriteItemNBT extends ReadWriteNBT, ReadableItemNBT {

    /**
     * True, if the item has any tags now known for this item type.
     *
     * @return true when custom tags are present
     */
    public boolean hasCustomNbtData();

    /**
     * Remove all custom (non-vanilla) NBT tags from the NBTItem.
     */
    public void clearCustomNBT();


    public void modifyMeta(BiConsumer<ReadableNBT, ItemMeta> handler);


    public <T extends ItemMeta> void modifyMeta(Class<T> type, BiConsumer<ReadableNBT, T> handler);

}