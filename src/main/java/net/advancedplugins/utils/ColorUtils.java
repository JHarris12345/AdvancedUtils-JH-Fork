package net.advancedplugins.utils;

import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.utils.text.Text;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Pattern hexPattern = Pattern.compile("\\{#[a-fA-F0-9]{6}}");
    private static final Pattern normalPattern = Pattern.compile("([\u00A7&])[0-9a-fA-Fk-orK-OR]");
    private static final char AMPERSAND_CHAR = '&';
    private static final String COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
    private static final String FORMAT_CODES = "KkLlMmMnOo";

    /**
     * Replaces color codes with actual colors, and if on 1.16+, hex codes codes.
     *
     * @param string String to insert colors on.
     * @return String with colors.
     */
    public static String format(String string) {
        if (string == null || string.isEmpty()) return string;
        if (MinecraftVersion.getVersionNumber() >= 1_16_0) {
            string = gradient(string);
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
        strings.replaceAll(ColorUtils::format);
        return strings;
    }
    
    /**
     * Removes the colors (including hex) from the string
     * @param input String to remove the colors from
     * @return String without any colors
     */
    public static String stripColor(@NotNull String input) {
        return ChatColor.stripColor(input);
    }

    /**
     * Strips all colors off all elements of a list.
     *
     * @param strings List of strings to strip colors from.
     * @return List of strings with no colors.
     */
    public static List<String> stripColor(List<String> strings) {
        strings.replaceAll(ColorUtils::stripColor);
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


    // Code stolen (and edited) from https://www.spigotmc.org/threads/bungee-hex-color-util.561417/
    // :3
    private static String gradient(String msg) {
        while (msg.contains("<gradient") && msg.contains("</gradient>")) {
            int start = msg.indexOf("<gradient");

            char[] charArray = msg.toCharArray();
            StringBuilder formatCode = new StringBuilder();
            for (int i = 0; i < start - 1; i++) {
                if (charArray[i] == AMPERSAND_CHAR && COLOR_CODES.indexOf(charArray[i + 1]) > -1) {
                    if(FORMAT_CODES.indexOf(charArray[i+1]) > -1) {
                        formatCode.append(AMPERSAND_CHAR).append(charArray[i + 1]);
                    } else {
                        formatCode = new StringBuilder();
                    }
                }
                if(charArray[i] == '{' && charArray[i+1] == '#') {
                    formatCode = new StringBuilder();
                }
            }
            int gradientSize = 10;
            if(formatCode.length() > 0) {
                gradientSize += formatCode.length();
            }

            int firstHex = msg.indexOf("#", start);
            int secondHex = msg.indexOf("#", firstHex + 1);
            String hex1 = msg.substring(firstHex, firstHex + 7);
            String hex2 = msg.substring(secondHex, secondHex + 7);
            String str = msg.substring(msg.indexOf(">", start) + 1, msg.indexOf("</gradient>"));
            String toReplaceStart = msg.substring(start, msg.indexOf(">", start) + 1);
            String toReplaceEnd = "</gradient>";
            String newStr = str;
            for (int i = 0; i < str.length(); i++) {
                newStr = newStr.substring(0, i * gradientSize) + gradient(hex2, hex1, (float) i / (str.length() - 1))
                        + formatCode + newStr.substring(i * gradientSize);
            }
            String toReplace = toReplaceStart + str + toReplaceEnd;
            toReplace = toReplace.replace("?", "\\?");
            msg = msg.replaceFirst(
                    toReplace,
                    newStr);
        }
        return msg;
    }

    private static String gradient(String color1, String color2, float weight) {
        try {
            Color col1 = Color.decode(color1), col2 = Color.decode(color2);
            float w2 = 1 - weight;
            Color gradient = new Color(col1.getRed() / 255.0f * weight + col2.getRed() / 255.0f * w2,
                    col1.getGreen() / 255.0f * weight + col2.getGreen() / 255.0f * w2,
                    col1.getBlue() / 255.0f * weight + col2.getBlue() / 255.0f * w2);
            return String.format("{#%02x%02x%02x}", gradient.getRed(), gradient.getGreen(),
                    gradient.getBlue());
        } catch (Exception e) {
            return "{#FFFFFF}";
        }
    }
}
