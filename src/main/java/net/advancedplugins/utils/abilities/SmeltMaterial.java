package net.advancedplugins.utils.abilities;

import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class SmeltMaterial {

    public static ItemStack material(ItemStack i, boolean isSmelt) {
        if (!ASManager.isValid(i))
            return null;

        String material = i.getType().name();
        if (MinecraftVersion.isNew() && i.getType().isLegacy())
            material = ASManager.matchMaterial(material, 1, 0, true, false).getType().name();

        switch (material) {
            case "COBBLED_DEEPSLATE":
                return new ItemStack(Material.getMaterial("DEEPSLATE"));
            case "COBBLESTONE":
                return new ItemStack(Material.STONE);
            case "DEEPSLATE_COAL_ORE":
            case "COAL_ORE":
                return new ItemStack(Material.COAL);
            case "DEEPSLATE_IRON_ORE":
            case "RAW_IRON":
            case "IRON_ORE":
                if (!isSmelt) {
                    if (MinecraftVersion.getVersionNumber() > 1_16_5) {
                        return new ItemStack(Material.RAW_IRON);
                    } else  {
                        return new ItemStack(Material.getMaterial("IRON_ORE"));
                    }
                }
                return new ItemStack(Material.IRON_INGOT);
            case "DEEPSLATE_COPPER_ORE":
            case "RAW_COPPER":
            case "COPPER_ORE":
                return new ItemStack(Material.COPPER_INGOT);
            case "DEEPSLATE_GOLD_ORE":
            case "RAW_GOLD":
            case "GOLD_ORE":
                if (!isSmelt) {
                    if (MinecraftVersion.getVersionNumber() > 1_16_5) {
                        return new ItemStack(Material.RAW_GOLD);
                    } else  {
                        return new ItemStack(Material.getMaterial("GOLD_ORE"));
                    }
                }
                return new ItemStack(Material.GOLD_INGOT);
            case "DEEPSLATE_LAPIS_ORE":
            case "LAPIS_ORE":
                return new ItemStack(Material.LAPIS_LAZULI);
            case "DEEPSLATE_REDSTONE_ORE":
            case "REDSTONE_ORE":
                return new ItemStack(Material.REDSTONE, ThreadLocalRandom.current().nextInt(4) + 1);
            case "DEEPSLATE_DIAMOND_ORE":
            case "DIAMOND_ORE":
                return new ItemStack(Material.DIAMOND);
            case "DEEPSLATE_EMERALD_ORE":
            case "EMERALD_ORE":
                return new ItemStack(Material.EMERALD);
            case "QUARTZ_ORE":
                return new ItemStack(Material.QUARTZ);
            case "ANCIENT_DEBRIS":
                if (!isSmelt) return new ItemStack(Material.ANCIENT_DEBRIS);
                return new ItemStack(Material.NETHERITE_SCRAP);
            case "SAND":
                if (!isSmelt) return new ItemStack(Material.SAND);
                return new ItemStack(Material.GLASS);
            case "NETHERRACK":
                return new ItemStack(Material.matchMaterial("NETHER_BRICK"));
            case "CLAY":
            case "CLAY_ITEM":
                return new ItemStack(Material.matchMaterial("BRICK"));
            default:
                return new ItemStack(i);
        }
    }

    public static ItemStack material(Material m, boolean isSmelt) {
        if (m == null)
            return new ItemStack(Material.AIR);
        return material(new ItemStack(m), isSmelt);
    }
}
