package net.advancedplugins.utils;

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

            switch (name) {
                case "CONFUSION" -> aliases.add("NAUSEA");
                case "DAMAGE_RESISTANCE" -> {
                    aliases.add("RESISTANCE");
                    aliases.add("RES");
                }
                case "FAST_DIGGING" -> aliases.add("HASTE");
                case "FIRE_RESISTANCE" -> {
                    aliases.add("FIRE_RESISTANCE");
                    aliases.add("FIRE_RES");
                }
                case "HARM" -> aliases.add("HARMNESS");
                case "INCREASE_DAMAGE" -> {
                    aliases.add("STRENGTH");
                    aliases.add("STRENGHT");
                }
                case "JUMP" -> aliases.add("JUMP_BOOST");
                case "SLOW" -> aliases.add("SLOWNESS");
                case "BLINDNESS" -> aliases.add("BLIND");
            }

            if (!aliases.isEmpty()) {
                potionAliases.put(type, aliases);
            }
        }
    }
}
