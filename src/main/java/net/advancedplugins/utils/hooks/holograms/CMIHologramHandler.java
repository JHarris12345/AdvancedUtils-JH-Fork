package net.advancedplugins.utils.hooks.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class CMIHologramHandler extends HologramHandler {

    private HologramManager manager = null;

    public CMIHologramHandler(JavaPlugin plugin) {
        super(plugin);
        manager = ((CMI) Bukkit.getPluginManager().getPlugin("CMI")).getHologramManager();
    }

    @Override
    public String getName() {
        return "CMI";
    }

    @Override
    public void createHologram(Location loc, String name, String text) {
        CMIHologram holo = new CMIHologram(name, loc);
        holo.setLines(Arrays.asList(text));

        manager.addHologram(holo);
        holo.update();
    }

    @Override
    public void removeHologram(String name) {
        CMIHologram holo = manager.getByName(name);

        if (holo != null) {
            manager.removeHolo(holo);
        }
    }

    @Override
    public void updateHologram(String name, String text) {
        CMIHologram holo = manager.getByName(name);
        holo.setLines(Arrays.asList(text));
    }

}
