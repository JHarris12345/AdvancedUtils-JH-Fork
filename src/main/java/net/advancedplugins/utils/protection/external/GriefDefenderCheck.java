package net.advancedplugins.utils.protection.external;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.Subject;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.permission.flag.Flags;
import net.advancedplugins.utils.protection.ProtectionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;

public class GriefDefenderCheck implements ProtectionType {
    @Override
    public String getName() {
        return "GriefDefender";
    }

    public boolean canBreak(Player player, Location location) {
        // Get the claim at the specified location
        Claim claim = GriefDefender.getCore().getClaimAt(location);

        // If there is no claim or it's wilderness, allow breaking
        if (claim == null || claim.isWilderness()) {
            return true;
        }

        // Get the Subject representing the player
        Subject subject = GriefDefender.getCore().getUser(player.getUniqueId());

        // Check if the player has permission to break blocks in this claim
        Tristate result = GriefDefender.getPermissionManager().getActiveFlagPermissionValue(
                claim,
                subject,
                Flags.BLOCK_BREAK,
                null, // Source object (can be null for general checks)
                null, // Target object (can be null for general checks)
                Collections.emptySet() // No additional contexts
        );

        // Return true if permission is explicitly allowed, false otherwise
        return result == Tristate.TRUE;
    }

    public boolean isProtected(Location loc) {
        return GriefDefender.getCore().getClaimAt(loc) != null;
    }

    @Override
    public boolean canAttack(Player p, Player p2) {
        return true;
    }
}
