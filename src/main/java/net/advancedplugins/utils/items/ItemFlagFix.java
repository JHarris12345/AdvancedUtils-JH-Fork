package net.advancedplugins.utils.items;

import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class ItemFlagFix {
    // needs to be same all the time, otherwise can mess up item stacking
    private static final UUID uuid = UUID.fromString("bf6de271-f84e-477b-b0a1-cf020dddc32a");

    /**
     * Minecraft 1.20.6+ broke ItemFlags, so there is simple util to fix it
     * Use it before setting ItemFlag
     */
    public static void fix(ItemStack is) {
        if(!is.hasItemMeta()) return;

        if(!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) return;

        ItemMeta meta = is.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(
                uuid,
                "generic.follow_range",
                0.1,
                AttributeModifier.Operation.ADD_NUMBER
        );
        meta.addAttributeModifier(Attribute.GENERIC_FOLLOW_RANGE, modifier);

        is.setItemMeta(meta);
    }

    /**
     * Minecraft 1.20.6+ broke ItemFlags, so there is simple util to fix it
     * Use it before setting ItemFlag
     */
    public static void fix(ItemMeta meta) {
        if(!MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) return;

        AttributeModifier modifier = new AttributeModifier(
                uuid,
                "generic.follow_range",
                0.1,
                AttributeModifier.Operation.ADD_NUMBER
        );
        meta.addAttributeModifier(Attribute.GENERIC_FOLLOW_RANGE, modifier);
    }
}
