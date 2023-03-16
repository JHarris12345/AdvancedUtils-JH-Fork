package net.advancedplugins.utils.hooks.holograms;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class HologramHandler {

    private final JavaPlugin plugin;

    public HologramHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public double getOffset() {
        return 1;
    }

    public String getName() {
        return "?";
    }

    public void createHologram(Location loc, String name, String text) {
    }

    public void updateHologram(String name, String text) {
    }

    protected void removeFromList(String holoName) {
    }

    public void removeHologram(String name) {
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}


