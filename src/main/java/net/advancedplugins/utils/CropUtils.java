package net.advancedplugins.utils;

import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class CropUtils {

    private static final Map<String, String> materialConversions = new HashMap<>();

    static {
        materialConversions.put("FARMLAND", "SOIL");
        materialConversions.put("WHEAT_SEEDS", "SEEDS");
        materialConversions.put("WHEAT", "CROPS");
        materialConversions.put("CARROTS", "CARROT");
        materialConversions.put("CARROT", "CARROT_ITEM");
        materialConversions.put("POTATOES", "POTATO");
        materialConversions.put("POTATO", "POTATO_ITEM");
        materialConversions.put("NETHER_WART", "NETHER_WARTS");
    }

    /**
     * Converts new crops related materials to the legacy equivalent on legacy servers.
     *
     * @param mat 1.13+ name of material.
     * @return 1.13+ {@link Material} if on 1.13+, or legacy equivalent if on a legacy version.
     */
    public static Material convertToMaterial(String mat) {
        if (MinecraftVersion.isNew()) {
            try {
                return Material.valueOf(mat);
                // PITCHER_POD AND TORCHFLOWER IS ONLY AVAILABLE FROM 1.20
            } catch (IllegalArgumentException ignored) {
                return Material.AIR;
            }
        }
        for (Map.Entry<String, String> entry : materialConversions.entrySet()) {
            if (!mat.equals(entry.getKey()))
                continue;
            return Material.valueOf(mat.replace(entry.getKey(), entry.getValue()));
        }
        return Material.matchMaterial(mat);
    }

    /**
     * Calculates the proper amount of drops from crops.
     *
     * @param block    Block to get crop drops from.
     * @param dropType Type of the crops drops.
     * @param item     Item used to harvest the crop.
     * @return The amount of items to give the player.
     */
    public static int getDropAmount(Block block, Material dropType, ItemStack item) {
        if (!isFullyGrown(block)) return 1;

        int fortuneLevel = item.getEnchantmentLevel(VanillaEnchants.displayNameToEnchant("FORTUNE"));
        int amount;
        // The looting for most drops is calculated by binomial distribution:
        // 2 drops are fixed, then a drop is attempted three times with a success rate of around 57% to yield the extra 0\u00A73 drops.
        // Each level of Fortune enchantment increases the number of attempts by one. (Source: https://minecraft.fandom.com/wiki/)
        int distribution = new BinomialDistribution(Math.max(3, 3 + fortuneLevel), 0.57).sample();

        // The meanings of these values can be found on https://minecraft.fandom.com/wiki/.
        switch (block.getType().name().replace("LEGACY_", "")) {
            case "CARROT":
            case "CARROTS":
            case "POTATO":
            case "POTATOES": {
                amount = 2 + distribution;
                break;
            }
            case "BEETROOTS":
            case "BEETROOT": {
                if (dropType.name().contains("SEEDS")) {
                    amount = MathUtils.randomBetween(2, 4) + distribution;
                } else {
                    amount = 1;
                }
                break;
            }
            case "COCOA": {
                amount = 3;
                break;
            }
            case "NETHER_WART": {
                amount = new UniformIntegerDistribution(2, 4 + fortuneLevel).sample();
                break;
            }
            case "CROPS":
            case "WHEAT": {
                // When a fully-grown wheat crop is harvested, it drops 1 wheat and 0 to 3 wheat seeds.
                if (dropType.name().contains("SEEDS")) {
                    amount = MathUtils.randomBetween(0, 3) + distribution;
                } else {
                    amount = 1;
                }
                break;
            }
            default: {
                amount = 1;
                break;
            }
        }
        return amount;
    }

    /**
     * Checks if the provided material is a crop.
     */
    public static boolean isCrop(Material material) {
        if (!ASManager.isValid(material)) {
            return false;
        }

        String name = material.name();

        switch (name) {
            case "CROPS":
            case "WHEAT":
            case "CARROTS":
            case "POTATOES":
            case "BEETROOTS":
            case "CARROT":
            case "POTATO":
            case "BEETROOT":
            case "COCOA":
            case "NETHER_WART":
            case "NETHER_WARTS":
            case "TORCHFLOWER":
            case "PITCHER_CROP":
                return true;
        }

        return false;
    }

    /**
     * Checks if a crop is fully grown.
     */
    public static boolean isFullyGrown(Block b) {
        if (!ASManager.isValid(b) || !isCrop(b.getType())) return false;
        if (b.getType().name().equals("TORCHFLOWER")) {
            return true;
        }
        if (!(b.getBlockData() instanceof Ageable))
            return false;
        Ageable a = (Ageable) b.getBlockData();
        return a.getAge() == a.getMaximumAge();
    }

    /**
     * Returns true if the material is gained by planting a seed.
     */
    public static boolean isSeeded(Material m) {
        return getSeed(m) != m;
    }

    /**
     * Gets the seed needed to plant a crop.
     */
    public static Material getSeed(Material m) {
        if (!ASManager.isValid(m)) return m;
        String name = m.name();
        switch (name) {
            case "WHEAT":
            case "CROPS":
                if (MinecraftVersion.isNew())
                    return Material.WHEAT_SEEDS;
                else
                    return Material.matchMaterial("SEEDS");
            case "BEETROOTS":
            case "BEETROOT":
                return Material.BEETROOT_SEEDS;
        }
        return m;
    }


    /**
     * Checks if a Material is wheat (1.8 - 1.13).
     */
    public static boolean isWheat(Material m) {
        if (!ASManager.isValid(m)) return false;
        String name = m.name();
        return name.equals("WHEAT") || name.equals("CROPS");
    }

    /**
     * Checks if a Material is a beetroot
     */
    public static boolean isBeetroot(Material m) {
        if (!ASManager.isValid(m)) return false;
        String name = m.name();
        return name.equals("BEETROOT") || name.equals("BEETROOTS");
    }

    /**
     * @return Random number between 2 and 5.
     */
    public static int getCropAmount() {
        return MathUtils.randomBetween(2, 5);
    }

    /**
     * @return Random number between 1 and 3.
     */
    public static int getSeedAmount() {
        return MathUtils.randomBetween(1, 3);
    }

}
