package net.advancedplugins.utils.protection.external;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import net.advancedplugins.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GriefDefenderCheck implements ProtectionType {
    @Override
    public String getName() {
        return "GriefDefender";
    }

    @Override
    public boolean canBreak(Player p, Location b) {
        final Claim claim = GriefDefender.getCore().getClaimAt(b);

        if (claim != null && !claim.isWilderness()) {
            return claim.getPlayers().contains(p.getUniqueId());
        }
        return false;
    }

    public boolean isProtected(Location loc) {
        return GriefDefender.getCore().getClaimAt(loc) != null;
    }

    @Override
    public boolean canAttack(Player p, Player p2) {
        return true;
    }
}
