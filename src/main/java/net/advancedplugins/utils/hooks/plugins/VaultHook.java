package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PermissionHook;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook extends PluginHookInstance implements PermissionHook {
    private final Permission permission;

    public VaultHook() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        this.permission = rsp.getProvider();
    }

    @Override
    public boolean isEnabled() {
        return true;
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
}
