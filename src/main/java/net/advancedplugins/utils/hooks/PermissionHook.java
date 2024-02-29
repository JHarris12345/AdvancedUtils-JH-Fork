package net.advancedplugins.utils.hooks;

import org.bukkit.entity.Player;

public interface PermissionHook {

    boolean removePerm(Player player, String perm);
    boolean addPerm(Player player, String perm);
    boolean isPermEnabled();
}
