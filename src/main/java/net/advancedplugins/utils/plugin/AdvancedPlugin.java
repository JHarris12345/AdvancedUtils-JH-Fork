package net.advancedplugins.utils.plugin;

import lombok.Getter;
import lombok.Setter;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.FoliaScheduler;
import net.advancedplugins.utils.files.ResourceFileManager;
import net.advancedplugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Level;

public class AdvancedPlugin extends JavaPlugin implements Listener {


    @Getter
    private static AdvancedPlugin instance;

    private String startupError = null;
    private String pluginName = "";
    @Getter
    @Setter
    private boolean loaded = false;

    public void startup() throws Exception {
    }

    public void unload() throws Exception {
    }

    public void registerListeners() {
        // register listeners
    }

    public void registerCommands() {
        // register commands
    }

    @Override
    public void onDisable() {
        try {
            unload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        ASManager.setInstance(this);
        // bukkit.broadcast uncomment

        super.onEnable();
        pluginName = getDescription().getName();

        try {
            startup();
        } catch (Exception ev) {
            ev.printStackTrace();
            updateError(ev);
        }

        if (startupError != null)
            getServer().getPluginManager().registerEvents(this, this);
    }

    private void updateError(Exception ev) {
        if (ev.getClass().equals(ClassCastException.class)) {
            startupError = "Configuration error: A value of the wrong type was found. Please check your config files.";
        } else if (ev.getClass().equals(IOException.class)) {
            startupError = "File I/O error while loading configurations. Please check file permissions and paths, whether configuration file is not missing.";
        } else if (ev.getClass().equals(InvalidConfigurationException.class)) {
            startupError = "The configuration file is improperly formatted. Please verify the syntax of your config files (tools: https://yaml.helpch.at)";
        } else {
            startupError = "An unexpected error occurred while loading the plugin. ";
        }
    }

    public void registerEvents(Listener l) {
        Bukkit.getPluginManager().registerEvents(l, instance);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().isOp()) return;
        if (startupError == null) return;

        FoliaScheduler.runTaskLater(this, () -> {
            e.getPlayer().sendMessage(Text.modify("&c[" + pluginName + "] Unable to load the plugin correctly due to errors:"));
            e.getPlayer().sendMessage(Text.modify("&c&o" + startupError));
            e.getPlayer().sendMessage(Text.modify("&cIf the problem persists after checking the config files, please seek assistance at: https://discord.gg/advancedplugins"));
        }, 20);
    }

    public void saveFiles(String... files) {
        for (String file : files) {
            saveResource(file);
        }
    }

    public void saveResource(String resourcePath) {
        if (new File(getDataFolder(), resourcePath).isFile()) return;
        saveResource(resourcePath, false);
    }

    protected CompletableFuture<Void> initializeMaterialSupport(boolean async) {
        Executor executor = async ? new CompletableFuture<>().defaultExecutor() : Runnable::run;

        return CompletableFuture.runAsync(() -> {
            try {
                Material.matchMaterial("", true);
                getLogger().info("Legacy material support initialized. Ignore any error or warn message.");
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Cannot initialize legacy material support", e);
            }
        }, executor);
    }

    public void saveAllFiles(String folder) {
        if (!new File(getDataFolder(), folder).exists()) {
            ResourceFileManager.saveAllResources(this, folder, null);
        }
    }
}
