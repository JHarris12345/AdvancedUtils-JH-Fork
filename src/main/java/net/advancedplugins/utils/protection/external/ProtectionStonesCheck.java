package net.advancedplugins.utils.protection.external;

import dev.espi.protectionstones.PSRegion;
import net.advancedplugins.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionStonesCheck implements ProtectionType {
    @Override
    public String getName() {
        return "ProtectionStones";
    }

    @Override
    public boolean canBreak(Player p, Location b) {
        PSRegion region = PSRegion.fromLocation(b);
        if (region == null)
            return true;

        return region.isMember(p.getUniqueId()) || region.isOwner(p.getUniqueId());
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
