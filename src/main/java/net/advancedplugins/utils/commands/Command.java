package net.advancedplugins.utils.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class Command<T extends CommandSender> {
    protected final JavaPlugin plugin;
    private final String permission;
    private final boolean isConsole;

    private Function<T, String> noPermission;
    private Function<T, String> notOnline;

    public Command(JavaPlugin plugin, String permission, boolean isConsole) {
        this.plugin = plugin;
        this.permission = permission;
        this.isConsole = isConsole;
        this.noPermission = player -> "&cYou do not have permission to do this.";
    }

    public abstract void onExecute(T sender, String[] args);

    @SuppressWarnings("unchecked")
    public void middleMan(CommandSender sender, String[] args) {
        this.onExecute((T) sender, args);
    }

    public @Nullable String getPermission() {
        return this.permission;
    }

    public boolean isConsole() {
        return this.isConsole;
    }

    public void noPermissionLang(Function<T, String> noPermission) {
        this.noPermission = noPermission;
    }

    public void setNotOnlineLang(Function<T, String> notOnline) {
        this.notOnline = notOnline;
    }

    @SuppressWarnings("unchecked")
    public String getNoPermissionLang(CommandSender sender) {
        return this.noPermission.apply((T) sender);
    }

    @SuppressWarnings("unchecked")
    public String getNotOnlineLang(CommandSender sender) {
        return this.notOnline.apply((T) sender);
    }

    public boolean isPlayerOnline(CommandSender sender, Player p) {
        if (p == null || !p.isOnline()) {
            sender.sendMessage(this.notOnline.apply((T) sender));
            return false;
        }
        return true;
    }
}