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

            if (type.equals(PotionEffectType.CONFUSION)) {
                aliases.add("NAUSEA");
            } else if (type.equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                aliases.add("RESISTANCE");
                aliases.add("RES");
            } else if (type.equals(PotionEffectType.FAST_DIGGING)) {
                aliases.add("HASTE");
            } else if (type.equals(PotionEffectType.FIRE_RESISTANCE)) {
                aliases.add("FIRE_RESISTANCE");
                aliases.add("FIRE_RES");
            } else if (type.equals(PotionEffectType.HARM)) {
                aliases.add("HARMNESS");
            } else if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
                aliases.add("STRENGTH");
                aliases.add("STRENGHT");
            } else if (type.equals(PotionEffectType.JUMP)) {
                aliases.add("JUMP_BOOST");
            } else if (type.equals(PotionEffectType.SLOW)) {
                aliases.add("SLOWNESS");
            } else if (type.equals(PotionEffectType.BLINDNESS)) {
                aliases.add("BLIND");
            }

            if (!aliases.isEmpty()) {
                potionAliases.put(type, aliases);
            }
        }
    }
}
