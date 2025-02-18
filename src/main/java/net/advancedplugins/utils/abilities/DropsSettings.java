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
    private int dropsMultiplier = 1;

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

    /**
     * Whether the event is McMMO <a href="https://github.com/mcMMO-Dev/mcMMO/blob/master/src/main/java/com/gmail/nossr50/api/TreeFellerBlockBreakEvent.java#">TreeFellerEvent</a>
     */
    private boolean treeFellerEvent = false;

    /**
     * Whether the event is AureliumSkills <a href="https://github.com/Archy-X/AureliumSkills/blob/master/bukkit/src/main/java/com/archyx/aureliumskills/api/event/TerraformBlockBreakEvent.java">TerraformBlockBreakEvent</a>
     */
    private boolean terraformEvent = false;
}
