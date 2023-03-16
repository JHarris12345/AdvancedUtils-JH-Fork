package net.advancedplugins.utils;

import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Pattern hexPattern = Pattern.compile("\\{#[a-fA-F0-9]{6}}");
    private static final Pattern normalPattern = Pattern.compile("([\u00A7&])[0-9a-fA-Fk-orK-OR]");

    /**
     * Replaces color codes with actual colors, and if on 1.16+, hex codes codes.
     *
     * @param string String to insert colors on.
     * @return String with colors.
     */
    public static String format(String string) {
        if (string == null || string.isEmpty()) return string;
        if (MinecraftVersion.getVersionNumber() >= 1_16_0) {
            Matcher match = hexPattern.matcher(string);
            while (match.find()) {
                String hex = string.substring(match.start(), match.end());
                string = StringUtils.replace(string, hex, "" + ChatColor.of(hex.replace("{", "").replace("}", "")));
                match = hexPattern.matcher(string);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Replaces color codes with actual colors, and if on 1.16+, hex codes codes.
     *
     * @param strings List of strings to insert colors on.
     * @return List of strings with colors..
     */
    public static List<String> format(List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, format(strings.get(i)));
        }
        return strings;
    }

    /**
     * Strips all colors off all elements of a list.
     *
     * @param strings List of strings to strip colors from.
     * @return List of strings with no colors.
     */
    public static List<String> stripColor(List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, ChatColor.stripColor(strings.get(i)));
        }
        return strings;
    }

    /**
     * Gets the last known color of a string.
     *
     * @param string String to get last color from.
     * @return Regular or HEX color code of the last known color from the provided String.
     */
    public static String getLastColor(String string) {
        String lastKnownColor = "";
        Matcher m = hexPattern.matcher(string);
        while (m.find()) {
            lastKnownColor = string.substring(m.start(), m.end());
        }
        Matcher m2 = normalPattern.matcher(string);
        while (m2.find()) {
            lastKnownColor = string.substring(m2.start(), m2.end());
        }
        return lastKnownColor;
    }

}
