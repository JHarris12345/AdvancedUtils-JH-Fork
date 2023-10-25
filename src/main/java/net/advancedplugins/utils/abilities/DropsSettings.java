package net.advancedplugins.utils.abilities;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class DropsSettings {

    /**
     * If the blocks should be smelted when broken.
     * Example: If the block is a IRON_ORE, it will be smelted to IRON_INGOT.
     */
    private boolean smelt = false;

    /**
     * If the drops should be added to the players inventory.
     */
    private boolean addToInventory = false;

    /**
     * If the block is being broken with Silk Touch.
     */
    private boolean silkTouch = false;

    /**
     * If the block should drop its default drops.
     */
    private boolean defaultDrops = true;

    /**
     * Multiplier for the amount of drops.
     */
    private int dropsMultiplier = 0;

    /**
     * If the blocks should be broken.
     */
    private boolean breakBlocks = false;

    /**
     * If the blocks should drop experience.
     */
    private boolean dropExp = true;

    /**
     * Should durability damage be done to tool
     */
    private boolean durabilityDamage = true;

    /**
     * The tool to break the blocks with.
     */
    private ItemStack tool = null;
}
