package net.advancedplugins.utils.hooks.plugins;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.iface.RelationParticipator;
import lombok.SneakyThrows;
import net.advancedplugins.utils.hooks.factions.FactionsPluginHook;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class FactionsUUIDHook extends FactionsPluginHook {

    private final Method relationToLocationMethod;
    private final Method relationMethod;

    @SneakyThrows
    public FactionsUUIDHook() {
        // this is required otherwise on some forks it just doesn't see the methods
        this.relationToLocationMethod = FPlayer.class.getMethod("getRelationToLocation");
        this.relationMethod = FPlayer.class.getMethod("getRelationTo", RelationParticipator.class);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "FactionsUUID";
    }

    @SneakyThrows
    @Override
    public String getRelation(Player p1, Player p2) {
        com.massivecraft.factions.FPlayer fp = com.massivecraft.factions.FPlayers.getInstance().getByPlayer(p1);
        com.massivecraft.factions.FPlayer fp2 = com.massivecraft.factions.FPlayers.getInstance().getByPlayer(p2);

        return relationMethod.invoke(fp, fp2).toString();
    }

    @SneakyThrows
    @Override
    public String getRelationOfLand(Player p) {
        com.massivecraft.factions.FPlayer fp = com.massivecraft.factions.FPlayers.getInstance().getByPlayer(p);

        return relationToLocationMethod.invoke(fp).toString();
    }

}
