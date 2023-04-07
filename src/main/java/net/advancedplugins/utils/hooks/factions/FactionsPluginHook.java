package net.advancedplugins.utils.hooks.factions;

import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Player;

public class FactionsPluginHook extends PluginHookInstance {
    public String getRelation(Player p1, Player p2) {
        return "NEUTRAL";
    }

    public String getRelationOfLand(Player p) {
        return "null";
    }
}
