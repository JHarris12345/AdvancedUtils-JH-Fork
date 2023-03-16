package net.advancedplugins.utils.items;

import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Easily create itemstacks, without messing your hands.
 * <i>Note that if you do use this in one of your projects, leave this notice.</i>
 * <i>Please do credit me if you do use this in one of your projects.</i>
 *
 * @author NonameSL
 */
public class ItemBuilder {

    private ItemStack is;
    private ItemMeta im;

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param m The material to create the ItemBuilder with.
     */
    public ItemBuilder(Material m) {
        this(m, 1);
    }

    /**
     * Create a new ItemBuilder over an existing itemstack.
     *
     * @param is The itemstack to create the ItemBuilder over.
     */
    public ItemBuilder(ItemStack is) {
        this.is = is;
        im = is.getItemMeta();
    }

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param m      The material of the item.
     * @param amount The amount of the item.
     */
    public ItemBuilder(Material m, int amount) {
        is = new ItemStack(m, amount);
        im = is.getItemMeta();
    }

    /**
     * Create a new ItemBuilder from scratch.
     *
     * @param m          The material of the item.
     * @param amount     The amount of the item.
     * @param durability The durability of the item.
     */
    public ItemBuilder(Material m, int amount, byte durability) {
        is = new ItemStack(m, amount, durability);
        im = is.getItemMeta();
    }

    /**
     * Change the durability of the item.
     *
     * @param dur The durability to set it to.
     */
    public ItemBuilder setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    /**
     * Set the displayname of the item.
     *
     * @param name The name to change it to.
     */
    public ItemBuilder setName(String name) {
        im.setDisplayName(name);
        return this;
    }

    /**
     * Add an unsafe enchantment.
     *
     * @param ench  The enchantment to add.
     * @param level The level to put the enchant on.
     */
    public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
        im.addEnchant(ench, level, true);
        return this;
    }

    /**
     * Remove a certain enchant from the item.
     *
     * @param ench The enchantment to remove
     */
    public ItemBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    /**
     * Set the skull owner for the item. Works on skulls only.
     *
     * @param owner The name of the skull's owner.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta skullMeta = (SkullMeta) im;
            skullMeta.setOwner(owner);
        } catch (ClassCastException ignored) {
        }
        return this;
    }

    /**
     * Add an enchant to the item.
     *
     * @param ench  The enchant to add
     * @param level The level
     */
    public ItemBuilder addEnchantment(Enchantment ench, int level) {
        im.addEnchant(ench, level, true);
        return this;
    }

    /**
     * Add multiple enchants at once.
     *
     * @param enchantments The enchants to add.
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        is.addEnchantments(enchantments);
        return this;
    }

    /**
     * Sets infinity durability on the item by setting the durability to Short.MAX_VALUE.
     */
    public ItemBuilder setInfinityDurability() {
        is.setDurability(Short.MAX_VALUE);
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     */
    public ItemBuilder setLore(String... lore) {
        im.setLore(Arrays.asList(lore));
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     */
    public ItemBuilder setLore(List<String> lore) {
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     */
    public ItemBuilder removeLoreLine(String line) {
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line)) return this;
        lore.remove(line);
        im.setLore(lore);
        return this;
    }

    /**
     * Remove a lore line.
     *
     * @param index The index of the lore line to remove.
     */
    public ItemBuilder removeLoreLine(int index) {
        List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index > lore.size()) return this;
        lore.remove(index);
        im.setLore(lore);
        return this;
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     */
    public ItemBuilder addLoreLine(String line) {
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore = new ArrayList<>(im.getLore());
        lore.add(line);
        im.setLore(lore);
        return this;
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     * @param pos  The index of where to put it.
     */
    public ItemBuilder addLoreLine(String line, int pos) {
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        return this;
    }

    /**
     * Sets the color of a piece of leather armor piece or fireworks.
     *
     * @param color The color to set it to.
     */
    public ItemBuilder setColor(Color color) {
        if (im instanceof LeatherArmorMeta) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) im;
            armorMeta.setColor(color);
        } else if (im instanceof FireworkEffectMeta) {
            FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) im;
            fireworkMeta.setEffect(FireworkEffect.builder().withColor(color).build());
        }
        return this;
    }

    /**
     * Adds ItemFlags to the item.
     *
     * @param flags ItemFlags to add.
     */
    public ItemBuilder setItemFlags(ItemFlag... flags) {
        im.addItemFlags(flags);
        return this;
    }

    /**
     * Adds an Item Attribute to the item.
     *
     * @param attribute Attribute to add.
     * @param modifier  Attribute modifier to apply to the Attribute.
     */
    public ItemBuilder addAttribute(Attribute attribute, AttributeModifier modifier) {
        im.addAttributeModifier(attribute, modifier);
        return this;
    }



    /**
     * Sets the amount of items in the stack.
     *
     * @param amount Amount to set. Can't be less then 0 or more then 64.
     */
    public ItemBuilder setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    /**
     * Adds an ItemFlag to the item.
     *
     * @param flag ItemFlag to add.
     */
    public ItemBuilder addItemFlag(ItemFlag flag) {
        im.addItemFlags(flag);
        return this;
    }

    /**
     * Sets the items custom model data on 1.14+ servers.
     * If called on a server < 1.14 it won't do anything.
     *
     * @param integer Custom model data.
     */
    public ItemBuilder setCustomModelData(Integer integer) {
        if (MinecraftVersion.getVersionNumber() >= 1_14_0) {
            im.setCustomModelData(integer);
        }
        return this;
    }


    /**
     * Gets the ItemMeta from the item that's being built.
     *
     * @return The ItemMeta from the item that's being built.
     */
    public ItemMeta getItemMeta() {
        return im;
    }

    /**
     * Retrieves the ItemStack from the ItemBuilder.
     *
     * @return The ItemStack created/modified by the ItemBuilder instance.
     */
    public ItemStack toItemStack() {
        is.setItemMeta(im);
        return is;
    }


}
