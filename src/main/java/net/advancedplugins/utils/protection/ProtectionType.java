package net.advancedplugins.utils.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionType {

    String getName();

    boolean canBreak(Player p, Location b);

    boolean canAttack(Player p, Player p2);

    boolean isProtected(Location loc);
}
