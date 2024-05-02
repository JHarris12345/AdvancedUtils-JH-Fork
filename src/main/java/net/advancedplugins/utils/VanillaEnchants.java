package net.advancedplugins.utils;

import net.advancedplugins.ae.Core;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.enchantments.Enchantment;

import java.util.Locale;

public class VanillaEnchants {

    public static Enchantment displayNameToEnchant(String paramString) {
        return displayNameToEnchant(paramString, true);
    }

    public static Enchantment displayNameToEnchant(String paramString, boolean displayError) {
        paramString = paramString.toLowerCase(Locale.ROOT);

        Enchantment enchant = getEnchant(paramString);
        if (enchant != null)
            return enchant;

        enchant = getEnchant(paramString.replaceAll(" ", "_"));
        if (enchant != null)
            return enchant;

        enchant = getEnchant(paramString.replaceAll("_", "").replaceAll(" ", ""));
        if (enchant != null)
            return enchant;

        if (displayError)
            Core.getInstance().getLogger().warning("Invalid vanilla enchantment: " + paramString
                    + ". Enchantment names can be found here: " +
                    "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html");
        return null;
    }

    private static Enchantment getEnchant(String paramString) {
        switch (paramString.toLowerCase(Locale.ROOT)) {
            case "protection":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("PROTECTION") : Enchantment.getByName("PROTECTION_ENVIRONMENTAL");
            case "fire_protection":
            case "fireprotection":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("FIRE_PROTECTION") : Enchantment.getByName("PROTECTION_FIRE");
            case "feather_falling":
            case "featherfalling":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("FEATHER_FALLING") : Enchantment.getByName("PROTECTION_FALL");
            case "blast_protection":
            case "blastprotection":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("BLAST_PROTECTION") : Enchantment.getByName("PROTECTION_EXPLOSIONS");
            case "projectile_protection":
            case "projectileprotection":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("PROJECTILE_PROTECTION") : Enchantment.getByName("PROTECTION_PROJECTILE");
            case "respiration":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("RESPIRATION") : Enchantment.getByName("OXYGEN");
            case "aqua_affinity":
            case "aquaaffinity":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("AQUA_AFFINITY") : Enchantment.getByName("WATER_WORKER");
            case "throns":
            case "thorns":
                return Enchantment.THORNS;
            case "depth_strider":
            case "depthstrider":
                return Enchantment.DEPTH_STRIDER;
            case "frost_walker":
            case "frostwalker":
                return MinecraftVersion.getVersionNumber() >= 1_9_0 ? Enchantment.FROST_WALKER : null;
            case "curse_of_binding":
            case "curseofbinding":
                return MinecraftVersion.getVersionNumber() >= 1_11_0 ? Enchantment.BINDING_CURSE : null;
            case "soul_speed":
            case "soulspeed":
                return MinecraftVersion.getVersionNumber() >= 1_16_0 ? Enchantment.SOUL_SPEED : null;
            case "sharpness":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("SHARPNESS") : Enchantment.getByName("DAMAGE_ALL");
            case "smite":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("SMITE") : Enchantment.getByName("DAMAGE_UNDEAD");
            case "bane_of_arthropods":
            case "baneofarthropods":
            case "bane":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("BANE_OF_ARTHROPODS") : Enchantment.getByName("DAMAGE_ARTHROPODS");
            case "knockback":
                return Enchantment.KNOCKBACK;
            case "fire_aspect":
            case "fireaspect":
                return Enchantment.FIRE_ASPECT;
            case "looting":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("LOOTING") : Enchantment.getByName("LOOT_BONUS_MOBS");
            case "sweeping_edge":
            case "sweepingedge":
                return MinecraftVersion.getVersionNumber() >= 1_11_1 ? Enchantment.getByName("SWEEPING_EDGE") : null;
            case "efficiency":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("EFFICIENCY") : Enchantment.getByName("DIG_SPEED");
            case "silk_touch":
            case "silktouch":
                return Enchantment.SILK_TOUCH;
            case "unbreaking":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("UNBREAKING") : Enchantment.getByName("DURABILITY");
            case "fortune":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("FORTUNE") : Enchantment.getByName("LOOT_BONUS_BLOCKS");
            case "power":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("POWER") : Enchantment.getByName("ARROW_DAMAGE");
            case "punch":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("POWER") : Enchantment.getByName("ARROW_KNOCKBACK");
            case "flame":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("FLAME") : Enchantment.getByName("ARROW_FIRE");
            case "infinity":
                return MinecraftVersion.getVersionNumber() >= 1_20_5 ? Enchantment.getByName("INFINITY") : Enchantment.getByName("ARROW_INFINITE");
            case "luck_of_the_sea":
            case "luckofthesea":
                return MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4) ?
                        Enchantment.getByName("LUCK_OF_THE_SEA") : Enchantment.getByName("LUCK");
            case "lure":
                return Enchantment.LURE;
            case "curse_of_vanishing":
                return (MinecraftVersion.getVersionNumber() >= 1_14_0) ? Enchantment.VANISHING_CURSE : null;
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
            case "quickcharge":
                return (MinecraftVersion.getVersionNumber() >= 1_14_0) ? Enchantment.QUICK_CHARGE : null;
            case "piercing":
                return (MinecraftVersion.getVersionNumber() >= 1_14_0) ? Enchantment.PIERCING : null;
            case "mending":
                return MinecraftVersion.getVersionNumber() >= 1_9_0 ? Enchantment.MENDING : null;
            case "vanishing_curse":
            case "vanishingcurse":
                return Enchantment.VANISHING_CURSE;
            default:
                try {
                    return Enchantment.getByName(paramString.toUpperCase(Locale.ROOT));
                } catch (Exception e) {
                    try {
                        return Enchantment.getByName(paramString.toUpperCase(Locale.ROOT).replace(" ", "")
                                .replace("_", ""));
                    } catch (Exception e2) {
                        return null;
                    }
                }
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
