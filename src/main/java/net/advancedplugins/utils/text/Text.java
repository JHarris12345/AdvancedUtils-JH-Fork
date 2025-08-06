package net.advancedplugins.utils.text;

import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Text {
    // some plugins may use minimessage, and they could clash with this
    public static boolean RENDER_GRADIENT = true;
    private static final char SECTION_CHAR = '\u00A7';
    private static final char AMPERSAND_CHAR = '&';
    private static final String COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
    private static final String FORMAT_CODES = "KkLlMmMnOo";
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + SECTION_CHAR + "[0-9A-FK-OR]");
    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#[a-fA-F0-9]{6}}");

    /**
     * Sends a message to a player, inserts placeholders and replaces colours.
     *
     * @param recipient The CommandSender to send the message to.
     * @param message   The message to send to the CommandSender.
     */
    public static void sendMessage(CommandSender recipient, String message) {
        Supplier<String> processor = () -> {
            if (recipient instanceof OfflinePlayer && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                return PlaceholderAPI.setPlaceholders((OfflinePlayer) recipient, message);
            }
            return message;
        };
        recipient.sendMessage(modify(processor.get()));
    }

    public static String parsePapi(String message, OfflinePlayer player) {
        if (HooksHandler.getHook(HookPlugin.PLACEHOLDERAPI) != null) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    public static List<String> parsePapi(List<String> messages, OfflinePlayer player) {
        if (HooksHandler.getHook(HookPlugin.PLACEHOLDERAPI) != null) {
            return messages.stream().map(message -> me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message)).collect(Collectors.toList());
        }
        return messages;
    }

    /**
     * Sends a message to multiple players, inserts placeholders and replaces colours.
     *
     * @param recipients The CommandSenders to send the message to
     * @param message    The message to send to all the CommandSenders
     */
    public static void sendMessage(Collection<CommandSender> recipients, String message) {
        for (CommandSender commandSender : recipients) {
            String[] lines = message.split("\n");
            for (String line : lines) {
                sendMessage(commandSender, line);
            }
        }
    }

    /**
     * Applies colours
     *
     * @param string The string to modify
     * @return A string which has been modified replacing colours ("&amp;") for use in Minecraft
     */
    public static String modify(String string) {
        return modify(string, null);
    }

    /**
     * Applies colours and replaces certain internally set placeholders
     *
     * @param string   The string to modify
     * @param replacer The replacer to apply to the string.
     * @return A string which has been modified replacing colours ("&amp;") for use in Minecraft
     */
    public static String modify(String string, Replace replacer) {
       return modify(string,replacer,true);
    }

    /**
     * Applies colours and replaces certain internally set placeholders
     *
     * @param string   The string to modify
     * @param replacer The replacer to apply to the string.
     * @param addPapi Should add papi
     * @return A string which has been modified replacing colours ("&amp;") for use in Minecraft
     */
    public static String modify(String string, Replace replacer, boolean addPapi) {
        if(HooksHandler.getHook(HookPlugin.PLACEHOLDERAPI) != null && string != null && addPapi) {
            // done because Player expansion for PAPI is dumb and returns empty string for any placeholder starting with %player
            // instead of not parsing it at all (Wega)
            // https://github.com/PlaceholderAPI/Player-Expansion/blob/master/src/main/java/com/extendedclip/papi/expansion/player/PlayerExpansion.java#L112

            // same for LuckPerms :)
            // https://github.com/LuckPerms/placeholders/blob/master/bukkit-placeholderapi/src/main/java/me/lucko/luckperms/placeholders/LuckPermsExpansion.java

            // NOTE: If this becomes an issue with more expansions, just create a separate getMethods in LocaleHandler
            // which have a parser, so the player data is actually parsed instead of being parsed will null parser.
            // This will only work for values which are player specific though and are parsed at the exact runtime.
            // (Wega)
            string = string
                    .replace("%player", "%playertemp")
                    .replace("%luckperms", "%luckpermstemp");
            string = parsePapi(string, null);
            string = string
                    .replace("%playertemp", "%player")
                    .replace("%luckpermstemp", "%luckperms");
        }
        return string == null ? null : renderColorCodes(replacer == null ? string : replacer.apply(new Replacer()).applyTo(string));
    }

    /**
     * Applies colours
     *
     * @param list The strings to modify
     * @return A string list which has been modified replacing colours ("&amp;") for use in Minecraft
     */
    public static List<String> modify(List<String> list) {
        return modify(list, null);
    }

    /**
     * Applies colours and replaces certain internally set placeholders
     *
     * @param list     The strings to modify
     * @param replacer The replacer to apply to the string.
     * @return A string which has been modified replacing colours ("&amp;") for use in Minecraft
     */
    public static List<String> modify(List<String> list, Replace replacer) {
        return modify(list, replacer,true);
    }

    /**
     * Applies colours and replaces certain internally set placeholders
     *
     * @param list     The strings to modify
     * @param replacer The replacer to apply to the string.
     * @param addPapi  Should add papi
     * @return A string which has been modified replacing colours ("&amp;") for use in Minecraft
     */
    public static List<String> modify(List<String> list, Replace replacer, boolean addPapi) {
        if (list == null) {
            return null;
        }
        List<String> middleList = Lists.newArrayList();
        for (String string : list) {
            String s = modify(string, replacer,addPapi);
            middleList.addAll(Arrays.stream(s.split("<new>")).collect(Collectors.toList()));
        }
        return middleList;
    }

    /**
     * Applies colours and replaces certain internally set placeholders on an item.
     *
     * @param itemStack The item to modify
     * @param replacer  The replacer to apply to the string.
     * @return An item which has been modified
     */
    public static ItemStack modify(ItemStack itemStack, Replace replacer) {
        ItemStack mutableItem = itemStack.clone();
        ItemMeta itemMeta = mutableItem.getItemMeta();
        if (itemMeta == null) {
            return mutableItem;
        }
        itemMeta.setDisplayName(modify(itemMeta.getDisplayName(), replacer));
        itemMeta.setLore(modify(itemMeta.getLore(), replacer));
        mutableItem.setItemMeta(itemMeta);
        return mutableItem;
    }

    /**
     * Removes colour codes from a string.
     *
     * @param string The string to remove colours from.
     * @return The string with colours removed.
     */
    public static String decolorize(String string) {
        return string == null ? null : unrenderColorCodes(string);
    }

    /**
     * Removes colour codes from a string.
     *
     * @param input The string to remove colours from.
     * @return The string with colours removed.
     */
    private static String unrenderColorCodes(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String renderColorCodes(String textToRender) {
        if (MinecraftVersion.getVersion().getVersionId() >= 1160)
            textToRender = RENDER_GRADIENT ? gradient(textToRender) : textToRender;{
            Matcher match = HEX_PATTERN.matcher(textToRender);
            while (match.find()) {
                String hex = textToRender.substring(match.start(), match.end());
                textToRender = StringUtils.replace(textToRender, hex, ""
                        + ChatColor.of(hex.replace("{", "").replace("}", "")));
                match = HEX_PATTERN.matcher(textToRender);
            }
        }

        char[] charArray = textToRender.toCharArray();
        for (int i = 0; i < charArray.length - 1; i++) {
            if (charArray[i] == Text.AMPERSAND_CHAR && COLOR_CODES.indexOf(charArray[i + 1]) > -1) {
                charArray[i] = Text.SECTION_CHAR;
                charArray[i + 1] = Character.toLowerCase(charArray[i + 1]);
            }
        }
        return new String(charArray);
    }

    // Code stolen (and edited) from https://www.spigotmc.org/threads/bungee-hex-color-util.561417/
    // Example format: <gradient:#HEX_FROM,#HEX_TO>text</gradient>
    // i.e. <gradient:#FF0000,#0000FF>text</gradient>
    // :3
    private static String gradient(String msg) {
        while (msg.contains("<gradient") && msg.contains("</gradient>")) {
            int start = msg.indexOf("<gradient");

            char[] charArray = msg.toCharArray();
            StringBuilder formatCode = new StringBuilder();
            for (int i = 0; i < start - 1; i++) {
                if (charArray[i] == Text.AMPERSAND_CHAR && COLOR_CODES.indexOf(charArray[i + 1]) > -1) {
                    if(FORMAT_CODES.indexOf(charArray[i+1]) > -1) {
                        formatCode.append(Text.AMPERSAND_CHAR).append(charArray[i + 1]);
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