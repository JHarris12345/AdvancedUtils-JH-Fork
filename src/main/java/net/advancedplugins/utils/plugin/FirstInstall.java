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
                        "&fThank you for installing &b&l" + pluginName + "&f!"));
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7- &fNeed help? Join our community: &bhttps://discord.gg/advancedplugins"));
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7- &fEnhance your experience with our UI overhaul by installing: &b" + addonURL));
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7- &fLooking for the best Minecraft hosting? &aMintServers&f " +
                                "offers &aUNLIMITED RAM&f and top-notch performance: &bhttps://mintservers.com&f"));
            }, 20L);

            // unregister the event
            PlayerJoinEvent.getHandlerList().unregister(this);
        }
    }

    public static void checkFirstInstall(JavaPlugin plugin, String configFile, String addonURL) {
        checkFirstInstall(plugin, configFile, addonURL, null);
    }

    public static void checkFirstInstall(JavaPlugin plugin, String configFile, String addonURL, String override) {
        UpdateChecker.checkUpdate(plugin);
        if (new File(plugin.getDataFolder(), configFile).exists()) {
            return;
        }

        FirstInstall.announce = true;
        FirstInstall.pluginName = plugin.getName();
        FirstInstall.addonURL = addonURL;
        FirstInstall.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new FirstInstall(), plugin);

        // Now send the message in console as well
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&fThank you for installing &b&l" + plugin.getName() + "&f!"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7- &fNeed help? Join our community: &bhttps://discord.gg/advancedplugins"));
        if (override == null) {
            if (addonURL != null)
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&7- &fEnhance your experience with our UI overhaul by installing: &b" + addonURL));
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    override));
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7- &fLooking for the best Minecraft hosting? &aMintServers&f " +
                        "offers &aUNLIMITED RAM&f and top-notch performance: &bhttps://mintservers.com&f"));

    }

    public static void sendStartupAlert(JavaPlugin plugin, String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&a>> &f" + message));

        //  Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
        //          "&a>> &fLooking for the best Minecraft hosting? &aMintServers&f " +
        //                 "offers &aUnlimited RAM&f and &aHigh Performance&f for only $9.99/mo: &ahttps://mintservers.com&f"));
    }
}
