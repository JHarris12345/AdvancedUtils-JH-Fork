package net.advancedplugins.utils.hooks.plugins;

import lombok.SneakyThrows;
import net.advancedplugins.utils.hooks.factions.FactionsPluginHook;
import org.bukkit.entity.Player;

public class FactionsUUIDHook extends FactionsPluginHook {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "FactionsUUID";
    }

    @Override
    public String getRelation(Player p1, Player p2) {
        com.massivecraft.factions.FPlayer fp = com.massivecraft.factions.FPlayers.getInstance().getByPlayer(p1);
        com.massivecraft.factions.FPlayer fp2 = com.massivecraft.factions.FPlayers.getInstance().getByPlayer(p2);

        return fp.getRelationTo(fp2).toString();
    }

    @SneakyThrows
    @Override
    public String getRelationOfLand(Player p) {
        com.massivecraft.factions.FPlayer fp = com.massivecraft.factions.FPlayers.getInstance().getByPlayer(p);
        return fp.getClass().getMethod("getRelationToLocation").invoke(fp).toString();
    }
}
