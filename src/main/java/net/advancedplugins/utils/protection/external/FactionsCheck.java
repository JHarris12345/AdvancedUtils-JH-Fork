package net.advancedplugins.utils.protection.external;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.hooks.factions.FactionsPluginHook;
import net.advancedplugins.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionsCheck implements ProtectionType {
    @Override
    public String getName() {
        return "Factions";
    }

    @Override
    public boolean canBreak(Player p, Location b) {
        FactionsPluginHook hook = (FactionsPluginHook) HooksHandler.getHook(HookPlugin.FACTIONS);
        if (!hook.isEnabled())
            return true;
        String relation = hook.getRelationOfLand(p);

        return relation.equalsIgnoreCase("Wilderness") || relation.equalsIgnoreCase("null")
                || relation.equalsIgnoreCase("neutral") || relation.equalsIgnoreCase("member");
    }

    @Override
    public boolean isProtected(Location loc) {
        return false;
    }

    @Override
    public boolean canAttack(Player p, Player p2) {
        return true;
    }
}
