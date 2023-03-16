package net.advancedplugins.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayASound {

    private static Sound getSound(String sound) {
        if (sound == null) return null;
        if (sound.equalsIgnoreCase("none")) return null;
        if (sound.isEmpty()) return null;
        Sound s;
        try {
            s = Sound.valueOf(sound);
        } catch (IllegalArgumentException ev) {
            if (sound.contains("_")) {
                String[] data = sound.split("_");
                if (data.length < 3) {
                    String switched = data[1] + "_" + data[0];
                    try {
                        return Sound.valueOf(switched);
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                }
            }
            return null;
        }
        return s;
    }

    private static final List<String> warnedSounds = new ArrayList<>();

    private static void warn(String sound) {
        if (warnedSounds.add(sound))
            return;
        Bukkit.getLogger().warning("Sound " + sound + " couldn't be found: invalid sound for this minecraft version?");
        warnedSounds.add(sound);
    }

    public static void playSound(String sound, Player p) {
        playSound(sound, p, 10.0f, 1.0f);
    }

    public static void playSound(String sound, Player p, float pitch, float volume) {
        Sound s = getSound(sound);
        if (s == null) {
            warn(sound);
            return;
        }
        p.playSound(p.getLocation(), s, volume, pitch);
    }

    public static void playSound(String sound, Location loc) {
        playSound(sound, loc, 1.0f, 1.0f);
    }

    public static void playSound(String sound, Location loc, float pitch, float volume) {
        Sound s = getSound(sound);
        if (s == null) {
            warn(sound);
            return;
        }
        loc.getWorld().playSound(loc, s, volume, pitch);
    }

}
