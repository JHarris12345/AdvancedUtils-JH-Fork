package net.advancedplugins.utils.hooks.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;

public class HolographicDisplaysHandler extends HologramHandler {

    private HashMap<String, Hologram> holograms = new HashMap<>();

    public HolographicDisplaysHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "HolographicDisplays";
    }

    @Override
    public void createHologram(Location loc, String name, String text) {
        Hologram hologram = HologramsAPI.createHologram(getPlugin(), loc);

        hologram.appendTextLine(text);
        holograms.put(name, hologram);
    }

    @Override
    public void removeHologram(String name) {
        holograms.get(name).delete();
    }

    @Override
    public void updateHologram(String name, String text) {
        Hologram h = holograms.get(name);
        h.removeLine(0);
        h.appendTextLine(text);
    }

}
