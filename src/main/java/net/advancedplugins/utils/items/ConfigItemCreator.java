package net.advancedplugins.utils.items;

import net.advancedplugins.utils.*;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.hooks.plugins.ItemsAdderHook;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class ConfigItemCreator {

    private static HashMap<String, String> cfgPaths = null;


    /**
     * Sets the default paths for getting item settings from a config file.
     */
    public static void setDefaultPaths(HashMap<String, String> newPaths) {
        cfgPaths = newPaths;
    }

    /**
     * Creates an item from a config file.
     *
     * @param filePath         File path starting from data folder. (eg. config.yml)
     * @param path             Config path to item settings base. (eg. enchantment-book.item)
     * @param placeholders     Placeholders to fill in. Key is the placeholder, value is what to replace it with.
     * @param pathReplacements Map used to override default paths for item settings.
     * @return ItemStack constructed from values from the config file.
     */

    static {
        HashMap<String, String> itemBuilderPaths = new HashMap<>();
        itemBuilderPaths.put("type", "type");
        itemBuilderPaths.put("id", "id");
        itemBuilderPaths.put("amount", "amount");
        itemBuilderPaths.put("name", "name");
        itemBuilderPaths.put("lore", "lore");
        itemBuilderPaths.put("item-flags", "item-flags");
        itemBuilderPaths.put("custom-model-data", "custom-model-data");
        itemBuilderPaths.put("force-glow", "force-glow");
        itemBuilderPaths.put("enchantments", "enchantments");
        itemBuilderPaths.put("custom-enchantments", "custom-enchantments");
        itemBuilderPaths.put("rgb-color", "rgb-color");
        itemBuilderPaths.put("itemsadder", "itemsadder");
        itemBuilderPaths.put("armor-trim", "armor-trim");
        itemBuilderPaths.put("unbreakable", "unbreakable");
        itemBuilderPaths.put("head", "head");
        itemBuilderPaths.put("owner", "head");
        ConfigItemCreator.setDefaultPaths(itemBuilderPaths);
    }

    @SuppressWarnings("unchecked")
    public static ItemStack fromConfigSection(String filePath, String path, Map<String, String> placeholders, Map<String, String> pathReplacements, JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + filePath);
        if (!file.exists()) {
            sendError("Unknown file!", filePath, null, null);
            return new ItemStack(Material.AIR);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return fromConfigSection(config, path, placeholders, pathReplacements);
    }

    public static ItemStack fromConfigSection(FileConfiguration config, ItemStack baseItem, String path, Map<String, String> placeholders, Map<String, String> pathReplacements) {
        return fromConfigSection(config.getConfigurationSection(""), baseItem, path, placeholders, pathReplacements);
    }

    public static ItemStack fromConfigSection(ConfigurationSection config, ItemStack baseItem, String path, Map<String, String> placeholders, Map<String, String> pathReplacements) {
        Map<String, String> paths = (Map<String, String>) cfgPaths.clone();
        String filePath = "config";

        if (pathReplacements != null && !pathReplacements.isEmpty()) {
            for (Map.Entry<String, String> entry : pathReplacements.entrySet()) {
                paths.put(entry.getKey(), entry.getValue());
            }
        }

        ItemBuilder builder = new ItemBuilder(baseItem);
        String typeStr = builder.toItemStack().getType().name();

        // Item name.
        if (config.contains(path + "." + paths.get("name"))) {
            String itemName = format(config.getString(path + "." + paths.get("name"), null), placeholders);
            builder.setName(itemName);
        }

        // Item lore.
        if (config.contains(path + "." + paths.get("lore"))) {
            List<String> lore = format(config.getStringList(path + "." + paths.get("lore")), placeholders);
            builder.setLore(lore);
        }

        // Item Flags.
        if (config.contains(path + "." + paths.get("item-flags"))) {
            List<String> itemFlags = format(config.getStringList(path + "." + paths.get("item-flags")), placeholders);
            for (String flagStr : itemFlags) {
                boolean isFlagValid = false;
                String requestedFlagName = flagStr.toUpperCase(Locale.ROOT);
                for (ItemFlag validFlag : ItemFlag.values()) {
                    if (validFlag.name().equals(requestedFlagName)) {
                        isFlagValid = true;
                        break;
                    }
                }
                if (!isFlagValid) {
                    sendError("Specified ItemFlag doesn't exist!", filePath, path, flagStr);
                } else {
                    builder.addItemFlag(ItemFlag.valueOf(flagStr));
                }
            }
        }

        // Armor trims
        if (config.contains(path + "." + paths.get("armor-trim"))) {
            String[] split = config.getString(path + "." + paths.get("armor-trim")).split(";");
            String trimMaterial = split[0];
            String trimPattern = split[1];

            builder.setArmorTrim(trimMaterial, trimPattern);
        }

        // Custom Model Data
        if (config.contains(path + "." + paths.get("custom-model-data"))) {
            if (MinecraftVersion.getVersionNumber() >= 1_14_0) {
                int modelData = config.getInt(path + "." + paths.get("custom-model-data"));
                builder.setCustomModelData(modelData);
            }

        }
        // Custom Model Data
        if (config.contains(path + "." + paths.get("unbreakable"))) {
            builder.setUnbreakable(config.getBoolean(path + "." + paths.get("unbreakable")));
        }


        // Enchantments
        if (config.contains(path + "." + paths.get("enchantments"))) {
            List<String> enchantments = format(config.getStringList(path + "." + paths.get("enchantments")), placeholders);
            for (String ench : enchantments) {
                Pair<String, Integer> pair = ASManager.parseEnchantment(ench);
                if (pair == null)
                    continue;

                Enchantment enchantment = VanillaEnchants.displayNameToEnchant(pair.getKey());
                if (enchantment == null) {
                    sendError("Specified vanilla enchantment doesn't exist!", filePath, path, pair.getKey());
                    continue;
                }

                builder.addUnsafeEnchantment(enchantment, pair.getValue());
            }
        }

        // Custom Enchantments
        if (config.contains(path + "." + paths.get("custom-enchantments"))) {
            List<String> enchantments = format(config.getStringList(path + "." + paths.get("custom-enchantments")), placeholders);
            for (String ench : enchantments) {
                Pair<String, Integer> pair = ASManager.parseEnchantment(ench);
                if (pair == null)
                    continue;

                builder.addCustomEnchantment(pair.getKey(), pair.getValue());
            }
        }

        // RGB Color
        if ((typeStr.contains("LEATHER_") || typeStr.contains("FIREWORK_STAR")) && config.contains(path + "." + paths.get("rgb-color"))) {
            String rgbStr = format(config.getString(path + "." + paths.get("rgb-color")), placeholders);
            String[] rgb = rgbStr.split(";");
            if (rgb.length != 3) {
                sendError("RGB color must contain 3 values in the format \"255;255;255\"!", filePath, path, rgbStr);
                return new ItemStack(Material.AIR);
            }

            if (!MathUtils.isInteger(rgb[0]) || !MathUtils.isInteger(rgb[1]) || !MathUtils.isInteger(rgb[2])) {
                sendError("RGB values must be between 0-255!", filePath, path, rgb[0]);
                return new ItemStack(Material.AIR);
            }

            int red = MathUtils.clamp(Integer.parseInt(rgb[0]), 0, 255),
                    green = MathUtils.clamp(Integer.parseInt(rgb[1]), 0, 255),
                    blue = MathUtils.clamp(Integer.parseInt(rgb[2]), 0, 255);

            Color color = Color.fromRGB(red, green, blue);

            if (typeStr.contains("LEATHER_")) {
                builder.setColor(color);
            } else if (typeStr.contains("FIREWORK_STAR")) {
                FireworkEffectMeta fireworkMeta = (FireworkEffectMeta) builder.getItemMeta();
                fireworkMeta.setEffect(FireworkEffect.builder().withColor(color).build());
            }
        }

        // This must be done last otherwise it will not glow
        if (config.contains(path + "." + paths.get("force-glow"))) {
            boolean forceGlow = config.getBoolean(path + "." + paths.get("force-glow"));
            if (forceGlow) {
                builder.setGlowing(true);
            }
        }

        return builder.toItemStack();
    }

    public static ItemStack fromConfigSection(FileConfiguration config, String path, Map<String, String> placeholders, Map<String, String> pathReplacements) {
        return fromConfigSection(config.getConfigurationSection(""), path, placeholders, pathReplacements);
    }

    public static ItemStack fromConfigSection(ConfigurationSection config, String path, Map<String, String> placeholders, Map<String, String> pathReplacements) {
        String filePath = "config";
        Map<String, String> paths = (Map<String, String>) cfgPaths.clone();

        if (pathReplacements != null && !pathReplacements.isEmpty()) {
            for (Map.Entry<String, String> entry : pathReplacements.entrySet()) {
                paths.put(entry.getKey(), entry.getValue());
            }
        }

        String t = config.getString(path + "." + paths.get("type"), null);
        String typeStr = t != null ? format(t, placeholders) : null;

        byte data = (byte) config.getInt(path + "." + paths.get("id"));
        int amount = MathUtils.clamp(ASManager.parseInt(config.getString(path + "." + paths.get("amount"), "1"), 1), 1, 64);

        Object advancedHead = config.get(path + ".advanced-heads");

        // Support for custom heads
        String head = config.getString(path + "." + paths.get("head"));

        // Support for itemsadder
        String itemsadder = config.getString(path + "." + paths.get("itemsadder"));

        ItemStack type;
        if (itemsadder != null) {
            type = ((ItemsAdderHook) HooksHandler.getHook(HookPlugin.ITEMSADDER)).getByName(itemsadder);
        } else if (head != null) {
            type = SkullCreator.itemFromBase64(head);
        } // else if (advancedHead != null)
//            type = net.advancedplugins.heads.api.AdvancedHeadsAPI.getHead(advancedHead);
        else
            type = ASManager.matchMaterial(typeStr, amount, data);

        if (type == null) {
            sendError("Specified material doesn't exist!", filePath, path, typeStr);
            return new ItemStack(Material.AIR);
        }

        return fromConfigSection(config, type, path, placeholders, pathReplacements);
    }

    /**
     * Replaces placeholders & inserts colors on provided String.
     *
     * @param string       String to format.
     * @param placeholders Placeholders to fill in.
     * @return Formatted string.
     */
    private static String format(String string, Map<String, String> placeholders) {
        string = placeholders(string, placeholders);
        string = ColorUtils.format(string);
        return string;
    }

    /**
     * Replaces placeholders & inserts colors on provided String List.
     *
     * @param strings      Strings to format.
     * @param placeholders Placeholders to fill in.
     * @return Formatted String List.
     */
    private static List<String> format(List<String> strings, Map<String, String> placeholders) {
        return ColorUtils.format(placeholders(strings, placeholders));
    }

    /**
     * Sets placeholders in a string.
     *
     * @param string       String to set placeholders in.
     * @param placeholders Map containing all placeholders and their values.
     * @return String with all placeholders filled in.
     */
    /**
     * Sets placeholders in a string.
     *
     * @param string       String to set placeholders in.
     * @param placeholders Map containing all placeholders and their values.
     * @return String with all placeholders filled in.
     */
    private static String placeholders(String string, Map<String, String> placeholders) {
        if (placeholders != null && string != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                if (string.contains("%description%") && entry.getKey().contains("%description%") && entry.getValue().contains("\n")) {
                    String formatString = string;
                    string = "";

                    String[] lines = entry.getValue().split("\n");
                    StringBuilder stringBuilder = new StringBuilder(string);
                    for (int i = 0; i < lines.length; i++) {
                        stringBuilder.append(formatString.replace(entry.getKey(), lines[i]));
                        if (i + 1 != lines.length) {
                            stringBuilder.append("\n");
                        }
                    }
                    string = stringBuilder.toString();
                } else if (string.contains("%level-description%") && entry.getKey().contains("%level-description%") && entry.getValue().contains("\n")) {
                    String formatString = string;
                    string = "";

                    String[] lines = entry.getValue().split("\n");
                    StringBuilder stringBuilder = new StringBuilder(string);
                    for (int i = 0; i < lines.length; i++) {
                        stringBuilder.append(formatString.replace(entry.getKey(), lines[i]));
                        if (i + 1 != lines.length) {
                            stringBuilder.append("\n");
                        }
                    }
                    string = stringBuilder.toString();
                } else {
                    string = string.replace(entry.getKey(), entry.getValue());
                }
            }
        }
        return string;
    }

    /**
     * Sets placeholders for each string in a list.
     *
     * @param strings      List of strings to set placeholders in.
     * @param placeholders Map containing all placeholders and their values.
     * @return String list with all placeholders filled in.
     */
    private static List<String> placeholders(List<String> strings, Map<String, String> placeholders) {
        List<String> strings1 = new ArrayList<>();
        if (placeholders != null) {
            for (String str : strings) {
                str = placeholders(str, placeholders);
                if (str.contains("\n")) {
                    String[] aa = str.split("\\n");
                    String lastColor = "";
                    for (String a : aa) {
                        strings1.add(lastColor + a);
                        lastColor = ColorUtils.getLastColor(a);
                    }
                } else {
                    strings1.add(str);
                }
            }
        } else {
            return strings;
        }

        return strings1;
    }

    /**
     * Adds a period to the end of a string if it isn't punctuated.
     */
    private static String addPunctuation(String string) {
        boolean hasPunctuation = string.endsWith(".") || string.endsWith("!") || string.endsWith("?");
        if (!hasPunctuation)
            string += ".";
        return string;
    }

    /**
     * Sends a message to console stating that something went wrong while creating an item.
     *
     * @param customMessage A message containing more information about why something went wrong.
     * @param file          The file name of the config.
     * @param configPath    The path to the value.
     * @param value         The value that caused an issue.
     */
    private static void sendError(String customMessage, String file, String configPath, Object value) {
        Bukkit.getLogger().severe("Something went wrong while creating an item! " + addPunctuation(customMessage) + " File: " + file + "  Config Path: " + configPath + "  Value: " + value);
    }


}
