package net.advancedplugins.utils.hooks.plugins;

import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.player.UserManager;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class McMMOHook extends PluginHookInstance {

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

    public boolean isFakeBlockBreak(Event e) {
        return false;
//        return e instanceof com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
    }

    public boolean callFakeEvent(Block b, Player p) {
//        com.gmail.nossr50.events.fake.FakeBlockBreakEvent event =
//                new com.gmail.nossr50.events.fake.FakeBlockBreakEvent(b, p);
//        Bukkit.getPluginManager().callEvent(event);
//
//        return !event.isCancelled();
        return true;
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
}
