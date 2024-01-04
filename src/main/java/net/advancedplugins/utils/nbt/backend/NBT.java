package net.advancedplugins.utils.nbt.backend;

import com.mojang.authlib.GameProfile;
import net.advancedplugins.utils.nbt.utils.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class NBT {

    private NBT() {
        // No instances of NBT. Utility class
    }

    /**
     * Get a read only instance of the items NBT. This method is slightly slower
     * than calling NBT.get due to having to create a copy of the ItemStack, but
     * allows context free access to the data.
     *
     * @param item
     * @return
     */
    public static ReadableNBT readNbt(ItemStack item) {
        return new NBTItem(item.clone(), false, true, false);
    }

    /**
     * It takes an ItemStack, and a function that takes a ReadableNBT and returns a
     * generic type T. It then returns the result of the function applied to a new
     * NBTItem
     *
     * @param item   The itemstack you want to get the NBT from
     * @param getter A function that takes a ReadableNBT and returns a value of type
     *               T.
     * @return The function is being returned.
     */
    public static <T> T get(ItemStack item, Function<ReadableItemNBT, T> getter) {
        NBTItem nbt = new NBTItem(item, false, true, false);
        T ret = getter.apply(nbt);
        if (ret instanceof ReadableNBT || ret instanceof ReadableNBTList<?>) {
            throw new NbtApiException("Tried returning part of the NBT to outside of the NBT scope!");
        }
        nbt.setClosed();
        return ret;
    }

    /**
     * It takes an ItemStack, and a Consumer that takes a ReadableNBT. Applies the
     * Consumer on the NBT of the item
     *
     * @param item The itemstack you want to get the NBT from
     */
    public static void get(ItemStack item, Consumer<ReadableItemNBT> getter) {
        NBTItem nbt = new NBTItem(item, false, true, false);
        getter.accept(nbt);
        nbt.setClosed();
    }




    /**
     * It takes an ItemStack, applies a function to its NBT, and returns the result
     * of the function
     *
     * @param item     The item you want to modify
     * @param function The function that will be applied to the item.
     * @return The return value of the function.
     */
    public static <T> T modify(ItemStack item, Function<ReadWriteItemNBT, T> function) {
        NBTItem nbti = new NBTItem(item, false, false, true);
        T val = function.apply(nbti);
        nbti.finalizeChanges();
        if (val instanceof ReadableNBT || val instanceof ReadableNBTList<?>) {
            throw new NbtApiException("Tried returning part of the NBT to outside of the NBT scope!");
        }
        nbti.setClosed();
        return val;
    }

    /**
     * It takes an ItemStack and a Consumer&lt;ReadWriteNBT&gt;, and then applies
     * the Consumer to the ItemStacks NBT
     *
     * @param item     The item you want to modify
     * @param consumer The consumer that will be used to modify the NBT.
     */
    public static void modify(ItemStack item, Consumer<ReadWriteItemNBT> consumer) {
        NBTItem nbti = new NBTItem(item, false, false, true);
        consumer.accept(nbti);
        nbti.finalizeChanges();
        nbti.setClosed();
    }



    /**
     * It converts an ItemStack to a ReadWriteNBT object
     *
     * @param itemStack The item stack you want to convert to NBT.
     * @return A ReadWriteNBT object.
     */
    public static ReadWriteNBT itemStackToNBT(ItemStack itemStack) {
        return NBTItem.convertItemtoNBT(itemStack);
    }

    /**
     * It converts a ReadableNBT object into an ItemStack
     *
     * @param compound The NBT tag to convert to an ItemStack
     * @return An ItemStack
     */
    public static ItemStack itemStackFromNBT(ReadableNBT compound) {
        return NBTItem.convertNBTtoItem((NBTCompound) compound);
    }

    /**
     * It converts an array of ItemStacks into a ReadWriteNBT object
     *
     * @param itemStacks The ItemStack[] you want to convert to NBT
     * @return An NBTItem object.
     */
    public static ReadWriteNBT itemStackArrayToNBT(ItemStack[] itemStacks) {
        return NBTItem.convertItemArraytoNBT(itemStacks);
    }

    /**
     * It converts a ReadableNBT object into an array of ItemStacks
     *
     * @param compound The NBT tag to convert to an ItemStack array.
     * @return An array of ItemStacks.
     */
    public static ItemStack[] itemStackArrayFromNBT(ReadableNBT compound) {
        return NBTItem.convertNBTtoItemArray((NBTCompound) compound);
    }

    /**
     * Create a new NBTContainer object and return it.
     *
     * @return A new instance of the NBTContainer class.
     */
    public static ReadWriteNBT createNBTObject() {
        return new NBTContainer();
    }

    /**
     * It takes a nbt json string, and returns a ReadWriteNBT object
     *
     * @param nbtString The NBT string to parse.
     * @return A new NBTContainer object.
     */
    public static ReadWriteNBT parseNBT(String nbtString) {
        return new NBTContainer(nbtString);
    }


    /**
     * It takes an ItemStack, applies a function to its NBT wrapped in a proxy, and
     * returns the result of the function
     *
     * @param item     The item you want to
     * @param wrapper  The target Proxy class
     * @param function The function that will be applied to the item.
     * @return The return value of the function.
     */
    public static <T, X extends NBTProxy> T modify(ItemStack item, Class<X> wrapper, Function<X, T> function) {
        NBTItem nbti = new NBTItem(item, false, false, true);
        T val = function.apply(new ProxyBuilder<>(nbti, wrapper).build());
        nbti.finalizeChanges();
        if (val instanceof ReadableNBT || val instanceof ReadableNBTList<?>) {
            throw new NbtApiException("Tried returning part of the NBT to outside of the NBT scope!");
        }
        nbti.setClosed();
        return val;
    }

    /**
     * It takes an ItemStack, applies a function to its NBT wrapped in a proxy.
     *
     * @param item     The item you want to modify
     * @param wrapper  The target Proxy class
     * @param consumer The consumer that will be used to modify the NBT.
     */
    public static <X extends NBTProxy> void modify(ItemStack item, Class<X> wrapper, Consumer<X> consumer) {
        NBTItem nbti = new NBTItem(item, false, false, true);
        consumer.accept(new ProxyBuilder<>(nbti, wrapper).build());
        nbti.finalizeChanges();
        nbti.setClosed();
    }


}