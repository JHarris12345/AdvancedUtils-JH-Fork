package net.advancedplugins.utils.text;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping of Minecraft color codes to MiniMessage tags
  */
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class MiniMessageConverter {

    @Getter
    private static final Map<String, String> colorMap = new HashMap<>();
    @Getter
    private static final Map<String, String> magicMap = new HashMap<>();

    static {
        // LEGACY CHAR
        colorMap.put("&0", "<black>");
        colorMap.put("&1", "<dark_blue>");
        colorMap.put("&2", "<dark_green>");
        colorMap.put("&3", "<dark_aqua>");
        colorMap.put("&4", "<dark_red>");
        colorMap.put("&5", "<dark_purple>");
        colorMap.put("&6", "<gold>");
        colorMap.put("&7", "<gray>");
        colorMap.put("&8", "<dark_gray>");
        colorMap.put("&9", "<blue>");
        colorMap.put("&a", "<green>");
        colorMap.put("&b", "<aqua>");
        colorMap.put("&c", "<red>");
        colorMap.put("&d", "<light_purple>");
        colorMap.put("&e", "<yellow>");
        colorMap.put("&f", "<white>");

        magicMap.put("&l", "<bold>");
        magicMap.put("&m", "<strikethrough>");
        magicMap.put("&n", "<underlined>");
        magicMap.put("&o", "<italic>");
        magicMap.put("&r", "<reset>");

        // SECTION CHAR
        colorMap.put("\u00A70", "<black>");
        colorMap.put("\u00A71", "<dark_blue>");
        colorMap.put("\u00A72", "<dark_green>");
        colorMap.put("\u00A73", "<dark_aqua>");
        colorMap.put("\u00A74", "<dark_red>");
        colorMap.put("\u00A75", "<dark_purple>");
        colorMap.put("\u00A76", "<gold>");
        colorMap.put("\u00A77", "<gray>");
        colorMap.put("\u00A78", "<dark_gray>");
        colorMap.put("\u00A79", "<blue>");
        colorMap.put("\u00A7a", "<green>");
        colorMap.put("\u00A7b", "<aqua>");
        colorMap.put("\u00A7c", "<red>");
        colorMap.put("\u00A7d", "<light_purple>");
        colorMap.put("\u00A7e", "<yellow>");
        colorMap.put("\u00A7f", "<white>");

        magicMap.put("\u00A7l", "<bold>");
        magicMap.put("\u00A7m", "<strikethrough>");
        magicMap.put("\u00A7n", "<underlined>");
        magicMap.put("\u00A7o", "<italic>");
        magicMap.put("\u00A7r", "<reset>");
    }

    public static String convertLegacy(@NotNull String legacyString) {
        for (Map.Entry<String, String> entry : colorMap.entrySet())
            legacyString = legacyString.replace(entry.getKey(), entry.getValue());
        for (Map.Entry<String, String> entry : magicMap.entrySet())
            legacyString = legacyString.replace(entry.getKey(), entry.getValue());
        System.out.println(colorMap);
        System.out.println("LEGACY = " + legacyString);
        return legacyString;
    }

    public static Component getFromLegacy(@NotNull String legacyString) {
        return MiniMessage.miniMessage().deserialize(convertLegacy(legacyString));
    }
}
