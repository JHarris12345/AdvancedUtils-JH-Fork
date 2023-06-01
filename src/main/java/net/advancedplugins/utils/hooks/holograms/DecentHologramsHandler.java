package net.advancedplugins.utils.hooks.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class DecentHologramsHandler extends HologramHandler {
    public DecentHologramsHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "DecentHolograms";
    }

    @Override
    public void createHologram(Location loc, String name, String text) {
        if (DHAPI.getHologram(name) != null) {
            return;
        }

        DHAPI.createHologram(name, loc, false, Arrays.asList(text));
    }

    @Override
    public void removeHologram(String name) {
        Hologram hologram = DHAPI.getHologram(name);

        if (hologram != null) {
            hologram.delete();
            DecentHologramsAPI.get().getHologramManager().removeHologram(name);
        }
    }

    @Override
    public void updateHologram(String name, String text) {
        Hologram hologram = DHAPI.getHologram(name);

        if (hologram != null) {
            DHAPI.setHologramLines(hologram, Arrays.asList(text));
        }
    }

}
