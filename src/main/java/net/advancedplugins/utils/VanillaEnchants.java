package net.advancedplugins.utils;

import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.enchantments.Enchantment;

import java.util.Locale;

public class VanillaEnchants {

    public static Enchantment displayNameToEnchant(String paramString) {
        switch (paramString.toLowerCase(Locale.ROOT)) {
            case "protection":
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            case "fire_protection":
                return Enchantment.PROTECTION_FIRE;
            case "feather_falling":
                return Enchantment.PROTECTION_FALL;
            case "blast_protection":
                return Enchantment.PROTECTION_EXPLOSIONS;
            case "projectile_protection":
                return Enchantment.PROTECTION_PROJECTILE;
            case "respiration":
                return Enchantment.OXYGEN;
            case "aqua_affinity":
                return Enchantment.WATER_WORKER;
            case "thorns":
                return Enchantment.THORNS;
            case "depth_strider":
                return Enchantment.DEPTH_STRIDER;
            case "frost_walker":
                return Enchantment.FROST_WALKER;
            case "curse_of_binding":
                return Enchantment.BINDING_CURSE;
            case "sharpness":
                return Enchantment.DAMAGE_ALL;
            case "smite":
                return Enchantment.DAMAGE_UNDEAD;
            case "bane_of_arthropods":
                return Enchantment.DAMAGE_ARTHROPODS;
            case "knockback":
                return Enchantment.KNOCKBACK;
            case "fire_aspect":
                return Enchantment.FIRE_ASPECT;
            case "looting":
                return Enchantment.LOOT_BONUS_MOBS;
            case "sweeping_edge":
                return Enchantment.SWEEPING_EDGE;
            case "efficiency":
                return Enchantment.DIG_SPEED;
            case "silk_touch":
                return Enchantment.SILK_TOUCH;
            case "unbreaking":
                return Enchantment.DURABILITY;
            case "fortune":
                return Enchantment.LOOT_BONUS_BLOCKS;
            case "power":
                return Enchantment.ARROW_DAMAGE;
            case "punch":
                return Enchantment.ARROW_KNOCKBACK;
            case "flame":
                return Enchantment.ARROW_FIRE;
            case "infinity":
                return Enchantment.ARROW_INFINITE;
            case "luck_of_the_sea":
                return Enchantment.LUCK;
            case "lure":
                return Enchantment.LURE;
            case "loyalty":
                return (MinecraftVersion.getVersionNumber() >= 1_13_0) ? Enchantment.LOYALTY : null;
            case "impaling":
                return (MinecraftVersion.getVersionNumber() >= 1_13_0) ? Enchantment.IMPALING : null;
            case "riptide":
                return (MinecraftVersion.getVersionNumber() >= 1_13_0) ? Enchantment.RIPTIDE : null;
            case "channeling":
                return (MinecraftVersion.getVersionNumber() >= 1_13_0) ? Enchantment.CHANNELING : null;
            case "multishot":
                return (MinecraftVersion.getVersionNumber() >= 1_14_0) ? Enchantment.MULTISHOT : null;
            case "quick_charge":
                return (MinecraftVersion.getVersionNumber() >= 1_14_0) ? Enchantment.QUICK_CHARGE : null;
            case "piercing":
                return (MinecraftVersion.getVersionNumber() >= 1_14_0) ? Enchantment.PIERCING : null;
            case "mending":
                return Enchantment.MENDING;
            case "vanishing_curse":
                return Enchantment.VANISHING_CURSE;
            default:
                return Enchantment.getByName(paramString.toUpperCase(Locale.ROOT));
        }
    }

    public static String enchantToDisplayName(Enchantment enchant) {
        switch (enchant.getName()) {
            case "PROTECTION_ENVIRONMENTAL":
                return "protection";
            case "PROTECTION_FIRE":
                return "fire_protection";
            case "PROTECTION_FALL":
                return "feather_falling";
            case "PROTECTION_EXPLOSIONS":
                return "blast_protection";
            case "PROTECTION_PROJECTILE":
                return "projectile_protection";
            case "OXYGEN":
                return "respiration";
            case "WATER_WORKER":
                return "aqua_affinity";
            case "THORNS":
                return "thorns";
            case "DEPTH_STRIDER":
                return "depth_strider";
            case "FROST_WALKER":
                return "frost_walker";
            case "BINDING_CURSE":
                return "curse_of_binding";
            case "SOUL_SPEED":
                return "soul_speed";
            case "DAMAGE_ALL":
                return "sharpness";
            case "DAMAGE_UNDEAD":
                return "smite";
            case "DAMAGE_ARTHROPODS":
                return "bane_of_arthropods";
            case "KNOCKBACK":
                return "knockback";
            case "FIRE_ASPECT":
                return "fire_aspect";
            case "LOOT_BONUS_MOBS":
                return "looting";
            case "SWEEPING_EDGE":
                return "sweeping_edge";
            case "DIG_SPEED":
                return "efficiency";
            case "SILK_TOUCH":
                return "silk_touch";
            case "DURABILITY":
                return "unbreaking";
            case "LOOT_BONUS_BLOCKS":
                return "fortune";
            case "ARROW_DAMAGE":
                return "power";
            case "ARROW_KNOCKBACK":
                return "punch";
            case "ARROW_FIRE":
                return "flame";
            case "ARROW_INFINITE":
                return "infinity";
            case "LUCK":
                return "luck_of_the_sea";
            case "LURE":
                return "lure";
            case "LOYALTY":
                return "loyalty";
            case "IMPALING":
                return "impaling";
            case "RIPTIDE":
                return "riptide";
            case "CHANNELING":
                return "channeling";
            case "MULTISHOT":
                return "multishot";
            case "QUICK_CHARGE":
                return "quick_charge";
            case "PIERCING":
                return "piercing";
            case "MENDING":
                return "mending";
            case "VANISHING_CURSE":
                return "vanishing_curse";
            default:
                return enchant.getName();
        }
    }

}
