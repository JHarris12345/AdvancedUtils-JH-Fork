package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PermissionHook;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultHook extends PluginHookInstance implements PermissionHook {
    private final @NotNull Permission permission;

    public VaultHook() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        // The original Vault plugin has a fallback Permission implementation
        // but VaultUnlocked doesn't have one!
        this.permission = rsp != null ? rsp.getProvider() : NoOpPermission.INSTANCE;
    }

    @Override
    public boolean isEnabled() {
        return permission != NoOpPermission.INSTANCE;
    }

    @Override
    public String getName() {
        return HookPlugin.VAULT.getPluginName();
    }

    @Override
    public boolean removePerm(Player player, String perm) {
        return permission.playerRemove(player, perm);
    }

    @Override
    public boolean addPerm(Player player, String perm) {
        return permission.playerAdd(player, perm);
    }

    @Override
    public boolean isPermEnabled() {
        return permission.isEnabled();
    }

    public static class NoOpPermission extends Permission {

        public static final NoOpPermission INSTANCE = new NoOpPermission();

        private NoOpPermission() {}

        @Override
        public String getName() {
            return "NoOpPermission";
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean hasSuperPermsCompat() {
            return false;
        }

        @Override
        public boolean playerHas(String s, String s1, String s2) {
            return false;
        }

        @Override
        public boolean playerAdd(String s, String s1, String s2) {
            return false;
        }

        @Override
        public boolean playerRemove(String s, String s1, String s2) {
            return false;
        }

        @Override
        public boolean groupHas(String s, String s1, String s2) {
            return false;
        }

        @Override
        public boolean groupAdd(String s, String s1, String s2) {
            return false;
        }

        @Override
        public boolean groupRemove(String s, String s1, String s2) {
            return false;
        }

        @Override
        public boolean playerInGroup(String s, String s1, String s2) {
            return false;
        }

        @Override
        public boolean playerAddGroup(String s, String s1, String s2) {
            return false;
        }

        @Override
        public boolean playerRemoveGroup(String s, String s1, String s2) {
            return false;
        }

        @Override
        public String[] getPlayerGroups(String s, String s1) {
            return new String[0];
        }

        @Override
        public String getPrimaryGroup(String s, String s1) {
            return "";
        }

        @Override
        public String[] getGroups() {
            return new String[0];
        }

        @Override
        public boolean hasGroupSupport() {
            return false;
        }
    }
}