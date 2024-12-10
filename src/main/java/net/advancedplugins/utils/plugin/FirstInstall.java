package net.advancedplugins.utils.plugin;

import net.advancedplugins.utils.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class FirstInstall implements Listener {

    // welcome the user to the plugin
    private static boolean announce = false;
    private static String addonURL;
    private static String pluginName;
    private static JavaPlugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("advancedplugins.admin")) {
            announce = false;

            // send the welcome message
            FoliaScheduler.runTaskLater(plugin, () -> {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&fThank you for installing &b&l" + pluginName + "&f! "));
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7- &fTo get help, join our discord server: &bhttps://discord.gg/advancedplugins"));
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7- &fWe recommend installing &b" + addonURL + "&f to enhance your experience with our UI overhaul!"));
            }, 20L);

            // unregister the event
            PlayerJoinEvent.getHandlerList().unregister(this);
        }
    }

    public static void checkFirstInstall(JavaPlugin plugin, String configFile, String addonURL) {
        checkFirstInstall(plugin, configFile, addonURL, null);
    }

    public static void checkFirstInstall(JavaPlugin plugin, String configFile, String addonURL, String override) {
        if (new File(plugin.getDataFolder(), configFile).exists()) {
            return;
        }

        FirstInstall.announce = true;
        FirstInstall.pluginName = plugin.getName();
        FirstInstall.addonURL = addonURL;
        FirstInstall.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new FirstInstall(), plugin);

        // now send the message in console as well
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&fThank you for installing &b&l" + plugin.getName() + "&f! "));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7- &fTo get help, join our discord server: &bhttps://discord.gg/advancedplugins"));
        if(override == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&7- &fWe recommend installing &b" + addonURL + "&f to enhance your experience with our UI overhaul!"));
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    override));
        }
    }
}
