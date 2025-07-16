package net.advancedplugins.utils.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemFlagFix {
    private static final UUID FIX_UUID = UUID.fromString("90787d5e-1940-4722-a91e-f0ba37f7c29d");

    /**
     * Minecraft 1.20.6+ broke ItemFlags, so there is simple util to fix it
     * Use it before setting ItemFlag
     */
    public static void fix(ItemStack is) {
        if (!is.hasItemMeta()) return;

        ItemMeta meta = is.getItemMeta();
        fix(meta);
        is.setItemMeta(meta);
    }

    /**
     * Minecraft 1.20.6+ broke ItemFlags, so there is simple util to fix it
     * Use it before setting ItemFlag
     */
    public static void fix(ItemMeta meta) {
        try {
            if (!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) return;

            // CAUSED THIS https://github.com/GC-spigot/AdvancedEnchantments/issues/4852
//            AttributeModifier modifier = new AttributeModifier(
//                    FIX_UUID,
//                    "generic.follow_range",
//                    0.1,
//                    AttributeModifier.Operation.ADD_NUMBER
//            );
//            if (meta.hasAttributeModifiers() && meta.getAttributeModifiers()
//                    .values().stream()
//                    .anyMatch(m -> m.equals(modifier))) return;
//
//            meta.addAttributeModifier(Attribute.GENERIC_FOLLOW_RANGE, modifier);
//            if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
//                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Automatically hide attributes to hide `generic.follow_range` attribute
//            }

            Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
            if (modifiers == null) {
                modifiers = HashMultimap.create();
                meta.setAttributeModifiers(modifiers);
            }
        } catch (Exception ev) {
            ev.printStackTrace();
        }
    }

    public static @NotNull ItemFlag[] hideAllAttributes() {
        // add these HIDE_ADDITIONAL_TOOLTIP
        //Setting to show/hide potion effects, book and firework information, map tooltips, patterns of banners, and enchantments of enchanted books.
        //HIDE_ARMOR_TRIM
        //Setting to show/hide armor trim from leather armor.
        //HIDE_ATTRIBUTES
        //Setting to show/hide Attributes like Damage
        //HIDE_DESTROYS
        //Setting to show/hide what the ItemStack can break/destroy
        //HIDE_DYE
        //Setting to show/hide dyes from colored leather armor.
        //HIDE_ENCHANTS
        //Setting to show/hide enchants
        //HIDE_PLACED_ON
        //Setting to show/hide where this ItemStack can be build/placed on
        //HIDE_UNBREAKABLE

        final List<ItemFlag> flags = Arrays.asList(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE
        );

        // Added in 1.20.5
        if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4))
            flags.add(ItemFlag.valueOf("HIDE_ADDITIONAL_TOOLTIP"));
        // Added in 1.19.4
        else if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_19_R3))
            flags.add(ItemFlag.valueOf("HIDE_ARMOR_TRIM"));
        // was replaced by HIDE_ADDITIONAL_TOOLTIP in 1.20.5
        else
            flags.add(ItemFlag.valueOf("HIDE_POTION_EFFECTS"));


        return flags.toArray(ItemFlag[]::new);
    }
}