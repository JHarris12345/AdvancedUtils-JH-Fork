package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;

public class McMMOHook extends PluginHookInstance {

    public int getSkillLevel(Player p, String skill) {
        return com.gmail.nossr50.api.ExperienceAPI.getLevel(p, skill);
    }

    @SuppressWarnings("deprecation")
    public void addSkillExperience(Player p, String skill, int skillExperience) {
        com.gmail.nossr50.api.ExperienceAPI.addXP(p, skill, skillExperience);
    }

    public boolean isBleeding(Player p) {
        return com.gmail.nossr50.api.AbilityAPI.isBleeding(p);
    }

    public List<String> getSkills() {
        return com.gmail.nossr50.api.SkillAPI.getSkills();
    }

    public boolean isFakeBlockBreak(Event e) {
        return e instanceof com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
    }

    public boolean callFakeEvent(Block b, Player p) {
        com.gmail.nossr50.events.fake.FakeBlockBreakEvent event =
                new com.gmail.nossr50.events.fake.FakeBlockBreakEvent(b, p);
        Bukkit.getPluginManager().callEvent(event);

        return !event.isCancelled();
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
