package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.ae.api.AEAPI;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class AdvancedSkillsHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "AdvancedSkills";
    }



}
