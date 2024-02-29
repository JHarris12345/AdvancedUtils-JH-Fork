package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PermissionHook;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;

public class LuckPermsHook extends PluginHookInstance implements PermissionHook {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.LUCKPERMS.getPluginName();
    }

    @Override
    public boolean removePerm(Player player, String perm) {
        UserManager manager = LuckPermsProvider.get().getUserManager();
        User user = manager.getUser(player.getUniqueId());
        boolean success = user.data().remove(PermissionNode.builder(perm).build()).wasSuccessful();
        manager.saveUser(user);
        return success;
    }

    @Override
    public boolean addPerm(Player player, String perm) {
        UserManager manager = LuckPermsProvider.get().getUserManager();
        User user = manager.getUser(player.getUniqueId());
        boolean success = user.data().add(PermissionNode.builder(perm).build()).wasSuccessful();
        manager.saveUser(user);
        return success;
    }

    @Override
    public boolean isPermEnabled() {
        return this.isEnabled();
    }
}
