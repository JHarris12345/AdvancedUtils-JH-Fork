package net.advancedplugins.utils.items;

import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class ItemFlagFix {
    private static final UUID FIX_UUID = UUID.fromString("90787d5e-1940-4722-a91e-f0ba37f7c29d");
    /**
     * Minecraft 1.20.6+ broke ItemFlags, so there is simple util to fix it
     * Use it before setting ItemFlag
     */
    public static void fix(ItemStack is) {
        if(!is.hasItemMeta()) return;

        ItemMeta meta = is.getItemMeta();
        fix(meta);
        is.setItemMeta(meta);
    }

    /**
     * Minecraft 1.20.6+ broke ItemFlags, so there is simple util to fix it
     * Use it before setting ItemFlag
     */
    public static void fix(ItemMeta meta) {
        if(!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) return;

        AttributeModifier modifier = new AttributeModifier(
                FIX_UUID,
                "generic.follow_range",
                0.1,
                AttributeModifier.Operation.ADD_NUMBER
        );
        if(meta.hasAttributeModifiers() && meta.getAttributeModifiers()
                .values().stream()
                .anyMatch(m -> m.equals(modifier))) return;

        meta.addAttributeModifier(Attribute.GENERIC_FOLLOW_RANGE, modifier);
        if(!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Automatically hide attributes to hide `generic.follow_range` attribute
        }
    }
}