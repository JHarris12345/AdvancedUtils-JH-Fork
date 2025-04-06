package net.advancedplugins.utils.hooks.plugins;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.api.TreeFellerBlockBreakEvent;
import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.fake.FakePlayerFishEvent;
import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.SchedulerUtils;
import net.advancedplugins.utils.abilities.SmeltMaterial;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class McMMOHook extends PluginHookInstance implements Listener {

    public McMMOHook() {
        SchedulerUtils.runTaskTimer(() -> {
            long currentTime = System.currentTimeMillis();
            // clear values older than 0.5 seconds
            treeFellerTelepathyBlocks.entrySet().removeIf(entry -> {
                if (currentTime - entry.getValue() > 500) {
                    entry.getKey().removeMetadata("ae-mcmmo-treefeller-tpdrops", ASManager.getInstance());
                    return true;
                }
                return false;
            });
        }, 20L, 20L);
    }

    public int getSkillLevel(Player p, String skill) {
        return com.gmail.nossr50.api.ExperienceAPI.getLevel(p, skill);
    }

    public void addSkillExperience(Player p, String skill, int skillExperience) {
        com.gmail.nossr50.api.ExperienceAPI.addXP(p, skill, skillExperience, "UNKNOWN");
    }

    public boolean isBleeding(Player p) {
        return com.gmail.nossr50.api.AbilityAPI.isBleeding(p);
    }

    public List<String> getSkills() {
        return com.gmail.nossr50.api.SkillAPI.getSkills();
    }

    public boolean isTreeFellerEvent(Event event) {
        return event instanceof TreeFellerBlockBreakEvent;
    }


    public boolean isFakeBlockBreak(Event e) {
        return false;
//        return e instanceof com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
    }

    public boolean isFakeFishEvent(Event e) {
        return e instanceof FakePlayerFishEvent;
    }

    public boolean callFakeEvent(Block b, Player p) {
//        com.gmail.nossr50.events.fake.FakeBlockBreakEvent event =
//                new com.gmail.nossr50.events.fake.FakeBlockBreakEvent(b, p);
//        Bukkit.getPluginManager().callEvent(event);
//
//        return !event.isCancelled();
        return true;
    }

    public boolean herbalismCheck(Player player, BlockBreakEvent event) {
        Block block = event.getBlock();
        BlockState blockState = block.getState();
        if (BlockUtils.affectedByGreenTerra(blockState)) {
            if (mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.HERBALISM)) {
                processHerbalismBlockBreakEvent(player, event);
                return true;
            }
        }
        return false;
    }
    /**
     * Will set the correct metadata for the blocks,
     * so they can be handled later
     *
     * @param player Player that broke the blocks
     * @param event The event
     */
    public void processHerbalismBlockBreakEvent(Player player, BlockBreakEvent event) {
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) return;

        mmoPlayer.getHerbalismManager().processHerbalismBlockBreakEvent(event);
    }

    // https://github.com/mcMMO-Dev/mcMMO/blob/master/src/main/java/com/gmail/nossr50/listeners/BlockListener.java#L400
    public void processBlockBreakEvent(Player player, BlockBreakEvent event, boolean telepathy, boolean smelt) {
        Block block = event.getBlock();
        BlockState blockState = block.getState();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (BlockUtils.affectedBySuperBreaker(blockState)
                && (ItemUtils.isPickaxe(heldItem) || ItemUtils.isHoe(heldItem))
                && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.MINING)
                && mcMMO.getChunkManager().isEligible(blockState)) {
            this.miningCheck(player, event, telepathy, smelt);
        }
    }

    // https://github.com/mcMMO-Dev/mcMMO/blob/master/src/main/java/com/gmail/nossr50/skills/mining/MiningManager.java#L78
    private void miningCheck(Player player, BlockBreakEvent event, boolean telepathy, boolean smelt) {
        Block block = event.getBlock();
        BlockState blockState = block.getState();
        McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) return;

        if (!mcMMO.p.getGeneralConfig().getDoubleDropsEnabled(PrimarySkillType.MINING, block.getType())) return;

        if (ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.MINING_DOUBLE_DROPS, mmoPlayer)) {
            boolean useTriple = mmoPlayer.getAbilityMode(mcMMO.p.getSkillTools().getSuperAbility(PrimarySkillType.MINING)) && mcMMO.p.getAdvancedConfig().getAllowMiningTripleDrops();

            BlockUtils.markDropsAsBonus(block, useTriple);

            if (block.getMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS).size() > 0) {
                BonusDropMeta bonusDropMeta = (BonusDropMeta) block.getMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS).get(0);
                int bonusCount = bonusDropMeta.asInt();

                for (ItemStack itemStack : block.getDrops()) {
                    for (int i = 0; i < bonusCount; i++) {
                        if (telepathy) blockState.setMetadata("ae_mcmmoTP_DROPS", new FixedMetadataValue(ASManager.getInstance(), true));
                        if (smelt) blockState.setMetadata("ae_mcmmoSMELT", new FixedMetadataValue(ASManager.getInstance(), true));

                        ItemUtils.spawnItems(event.getPlayer(), blockState.getLocation(), itemStack, itemStack.getAmount(), ItemSpawnReason.BONUS_DROPS);
                    }
                }
            }
        }
    }

    /*
     workaround for https://github.com/GC-spigot/AdvancedEnchantments/issues/4079
     stupid but it works :D
    */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBonusDrop(McMMOItemSpawnEvent event) {
        if (event.getItemSpawnReason() != ItemSpawnReason.BONUS_DROPS) return;
        Block block = event.getLocation().getBlock();

        if (block.hasMetadata("ae_mcmmoSMELT")) {
            block.removeMetadata("ae_mcmmoSMELT", ASManager.getInstance());

            ItemStack smelted = SmeltMaterial.material(event.getItemStack());
            if (smelted != null) {
                // Doesn't change the actual ItemStack dropped on the event
                event.setItemStack(smelted);

                // This is just a workaround and the above method only should be used once McMMO fixes this!
                if (!block.hasMetadata("ae_mcmmoTP_DROPS")) {
                    ASManager.dropItem(event.getLocation(), smelted);
                    event.getLocation().subtract(0, 10000, 0);
                }
            }
        }
        if (block.hasMetadata("ae_mcmmoTP_DROPS")) {
            block.removeMetadata("ae_mcmmoTP_DROPS", ASManager.getInstance());

            event.getLocation().subtract(0, 10000, 0);
            ItemStack bonusItem = event.getItemStack().clone();
            ASManager.giveItem(event.getPlayer(), bonusItem);
        }
    }

    public boolean blockHasHerbalismBonusDrops(Block block) {
        return block.hasMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS);
    }

    public int getHerbalismBonusDropMultiplier(Block block) {
        BonusDropMeta dropMeta = (BonusDropMeta) block.getMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS).get(0);
        return dropMeta.asInt();
    }

    @Override
    public String getName() {
        return "mcMMO";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static final Map<Block, Long> treeFellerTelepathyBlocks = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onTelepathyTreeFellerBonusItemSpawn(McMMOItemSpawnEvent event) {
        Block block = event.getLocation().getBlock();
        if (!treeFellerTelepathyBlocks.containsKey(block)) return;
        event.setCancelled(true);
        if (event.getItemSpawnReason() == ItemSpawnReason.BONUS_DROPS) {
            ASManager.giveItem(event.getPlayer(), event.getItemStack());
        }
    }
}
