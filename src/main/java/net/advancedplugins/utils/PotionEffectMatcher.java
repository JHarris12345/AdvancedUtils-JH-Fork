package net.advancedplugins.utils;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PotionEffectMatcher {

    private static final HashMap<PotionEffectType, List<String>> potionAliases = new HashMap<>();

    public static PotionEffectType matchPotion(String input) {
        try {
            input = input.toUpperCase(Locale.ROOT);
            if (potionAliases.isEmpty()) {
                init();
            }

            for (Map.Entry<PotionEffectType, List<String>> entry : potionAliases.entrySet()) {
                for (String s : entry.getValue()) {
                    if (s.equalsIgnoreCase(input)) {
                        return entry.getKey();
                    }
                }
            }
            return PotionEffectType.getByName(input);
        } catch (Exception ev) {
            ev.printStackTrace();
            return PotionEffectType.getByName(input);
        }
    }

    private static void init() {
        for (PotionEffectType type : PotionEffectType.values()) {
            List<String> aliases = new ArrayList<>();
            String name = type.getName().toUpperCase(Locale.ROOT);

            if (type.equals("CONFUSION")) {
                aliases.add("NAUSEA");
            } else if (type.equals("DAMAGE_RESISTANCE")) {
                aliases.add("RESISTANCE");
                aliases.add("RES");
            } else if (type.equals("FAST_DIGGING")) {
                aliases.add("HASTE");
            } else if (type.equals("FIRE_RESISTANCE")) {
                aliases.add("FIRE_RESISTANCE");
                aliases.add("FIRE_RES");
            } else if (type.equals("HARM")) {
                aliases.add("HARMNESS");
            } else if (type.equals("INCREASE_DAMAGE")) {
                aliases.add("STRENGTH");
                aliases.add("STRENGHT");
            } else if (type.equals("JUMP")) {
                aliases.add("JUMP_BOOST");
            } else if (type.equals("SLOW")) {
                aliases.add("SLOWNESS");
            } else if (type.equals("BLINDNESS")) {
                aliases.add("BLIND");
            }

            if (!aliases.isEmpty()) {
                potionAliases.put(type, aliases);
            }
        }
    }
}
