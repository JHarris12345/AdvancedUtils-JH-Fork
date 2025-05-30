package net.advancedplugins.utils.hooks.plugins;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.TOWNY.getPluginName();
    }

    public boolean canBuild(Player p, Location l) {
        return PlayerCacheUtil.getCachePermission(p, l, l.getBlock().getType(), TownyPermission.ActionType.BUILD);
    }

    public boolean canBreak(Player p, Location l) {
        return PlayerCacheUtil.getCachePermission(p, l, l.getBlock().getType(), TownyPermission.ActionType.DESTROY);
    }

    public boolean hasKeepInventory(Player p) {
        Resident resident = TownyAPI.getInstance().getResident(p);
        if (resident == null)
            return false;

        TownBlock tb = TownyAPI.getInstance().getTownBlock(p.getLocation());

        return getKeepInventoryValue(resident, tb);
    }

    private boolean getKeepInventoryValue(Resident resident, TownBlock tb) {
        // Run it this way so that we will override a plugin that has kept the
        // inventory, but they're in the wilderness where we don't want to keep
        // inventories.
        // Sometimes we keep the inventory when they are in any town.
        boolean keepInventory = TownySettings.getKeepInventoryInTowns() && tb != null;

        // All of the other tests require a town.
        if (tb == null)
            return keepInventory;

        if (resident.hasTown() && !keepInventory) {
            Town town = resident.getTownOrNull();
            Town tbTown = tb.getTownOrNull();
            // Sometimes we keep the inventory only when they are in their own town.
            if (TownySettings.getKeepInventoryInOwnTown() && tbTown.equals(town))
                keepInventory = true;
            // Sometimes we keep the inventory only when they are in a Town that considers them an ally.
            if (TownySettings.getKeepInventoryInAlliedTowns() && !keepInventory && tbTown.isAlliedWith(town))
                keepInventory = true;
        }

        // Sometimes we keep the inventory when they are in an Arena plot.
        if (TownySettings.getKeepInventoryInArenas() && !keepInventory && tb.getType() == TownBlockType.ARENA)
            keepInventory = true;

        return keepInventory;
    }
}
