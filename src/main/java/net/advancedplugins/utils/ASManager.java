package net.advancedplugins.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.advancedplugins.utils.annotations.ConfigKey;
import net.advancedplugins.utils.evalex.Expression;
import net.advancedplugins.utils.nbt.NBTapi;
import net.advancedplugins.utils.nbt.backend.ClassWrapper;
import net.advancedplugins.utils.nbt.backend.ReflectionMethod;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.utils.text.Replace;
import net.advancedplugins.utils.text.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.TurtleEgg;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.RoundingMode;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Ref;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ASManager {
    private static final HashSet<String> silkOnly = new HashSet<>(Arrays.asList("LEAVE", "LEAVES", "MUSHROOM_STEM", "TURTLE_EGG", "CORAL"));

    @Getter
    private static JavaPlugin instance;

    public static void setInstance(JavaPlugin instance) {
        ASManager.instance = instance;
    }

    private static final List<Integer> validSizes = new ArrayList<>(Arrays.asList(9, 18, 27, 36, 45, 54));

    public static int hexToDecimal(String s) {
        return Integer.parseInt(s, 16);
    }

    @Contract("null, _ -> fail")
    public static void notNull(Object arg, String identifier) {
        if (arg == null) {
            throw new IllegalArgumentException(identifier.concat(" cannot be null."));
        }
    }

    @Contract("!null, _ -> fail")
    public static void isNull(Object arg, String message) {
        if (arg != null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * {@link Bukkit#getPlayer(String)} includes {@link String#startsWith(String)} which could produce incorrect results. Like Tomousek and Tomousek2 both matching when searching for Tomousek.
     *
     * @param name Name to search for.
     * @return Player if found, null otherwise.
     */
    public static @Nullable Player getPlayerInsensitive(@NotNull String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Calculates the similarity percentage between two strings.
     *
     * @param s1 First string to compare.
     * @param s2 Second string to compare.
     * @return Returns the similarity percentage (int) between two strings.
     */
    public static int similarityPercentage(String s1, String s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty())
            return 0;

        int maxLength = Math.max(s1.length(), s2.length());
        int distance = StringUtils.getLevenshteinDistance(s1, s2);
        return (int) ((1 - ((double) distance / maxLength)) * 100);
    }

    public static int getInvSize(int size) {
        MathUtils.clamp(size, 9, 54);
        if (size % 9 != 0) {
            size = MathUtils.getClosestInt(size, validSizes);
        }
        return size;
    }

    public static boolean isSpawner(Material m) {
        return m.name().endsWith("SPAWNER");
    }

    public static boolean isSpawner(Block b) {
        if (b == null || b.getType() == null) return false;
        return isSpawner(b.getType());
    }

    public static boolean doesBlockFaceMatch(Block b, String endsWith, BlockFace... faces) {
        for (BlockFace face : faces) {
            Material material = b.getRelative(face).getType();
            if (isAir(material)) continue;
            if (material.name().endsWith(endsWith)) return true;
        }
        return false;
    }

    public static Block getOtherHalfOfBed(Block b) {
        if (!b.getType().name().endsWith("_BED")) return null;
        Bed bed = (Bed) b.getBlockData();
        Block face;
        if (bed.getPart() == Bed.Part.HEAD) face = b.getRelative(bed.getFacing().getOppositeFace());
        else face = b.getRelative(bed.getFacing());

        if (!(face.getBlockData() instanceof Bed)) return null;
        return face;
    }

    /**
     * Checks if a Material is a tool.
     *
     * @param material Material to check.
     * @return True of the material is a tool, false otherwise.
     */
    public static boolean isTool(Material material) {
        Object item = ReflectionMethod.CRAFT_MagicNumbers_getItem.run(null, material);
        return ASManager.isValid(material) && ClassWrapper.NMS_ItemTool.getClazz().isInstance(item);
    }

    public static boolean isExcessVelocity(Vector vel) {
        return (vel.getX() > 10.0D) || (vel.getX() < -10.0D) || (vel.getY() > 10.0D) || (vel.getY() < -10.0D) || (vel.getZ() > 10.0D) || (vel.getZ() < -10.0D);
    }

    public static List<Block> getBlocksFlat(Block start, int radius) {
        if (radius < 1) return (radius == 0) ? Collections.singletonList(start) : Collections.emptyList();
        int iterations = (radius << 1) + 1;
        List<Block> blocks = new ArrayList<>(iterations * iterations * iterations);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                blocks.add(start.getRelative(x, 0, z));
            }
        }
        return blocks;
    }

    /**
     * Gets the amount of a certain type of item a player has.
     *
     * @param player   Player's whose inventory to check.
     * @param material Item type to check.
     * @return Returns amount of items player has of specified type.
     */
    public static int getAmount(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null) continue;
            if (item.getType() != material) continue;

            count += item.getAmount();
        }

        return count;
    }

    /**
     * Checks if a player has a certain amount of items.
     *
     * @param p     Player's whose inventory to check.
     * @param m     Item type to check.
     * @param count Amount of items to check for.
     * @return Returns true if the player has the amount of items, false otherwise.
     */
    public static boolean hasAmount(Player p, Material m, int count) {
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null) continue;

            if (item.getType() != m) continue;

            count -= item.getAmount();
            if (count <= 0) return true;
        }

        // Added checks for off-hand (GC, 2024 Sept 17)
        if (p.getInventory().getItem(EquipmentSlot.OFF_HAND) != null && p.getInventory().getItem(EquipmentSlot.OFF_HAND).getType() == m) {
            count -= p.getInventory().getItem(EquipmentSlot.OFF_HAND).getAmount();
            if (count <= 0) return true;
        }

        return false;
    }

    /**
     * Removes specific amount of items from an inventory.
     *
     * @param inventory Inventory to remove items from.
     * @param material  Type of item to remove.
     * @param amount    Amount of items to remove.
     * @return True if all items were removed, false otherwise.
     */
    public static boolean removeItems(Inventory inventory, Material material, int amount) {
        if (material == null || inventory == null) return false;
        if (amount <= 0) return false;

        if (amount == Integer.MAX_VALUE) {
            inventory.remove(material);
            return true;
        }

        int toDelete = amount;
        if (inventory instanceof PlayerInventory) {
            PlayerInventory pInv = (PlayerInventory) inventory;
            ItemStack item = pInv.getItemInOffHand();
            if (item.getType() == material) {
                toDelete = removeItem(pInv, item, EquipmentSlot.OFF_HAND, toDelete);
            }
        }

        if (toDelete <= 0) return true;
        for (int i = 0; i < inventory.getSize(); i++) {
            int first = inventory.first(material);
            if (first == -1) return false;
            ItemStack item = inventory.getItem(first);
            assert item != null;
            toDelete = removeItem(inventory, item, first, toDelete);
            if (toDelete <= 0) {
                break;
            }
        }

        return true;
    }

    /**
     * Removes specific amount of items from an inventory.
     *
     * @param inventory Inventory to remove items from.
     * @param amount    Amount of items to remove.
     * @return True if all items were removed, false otherwise.
     */
    public static boolean removeItems(Inventory inventory, ItemStack itemStack, int amount) {
        if (itemStack == null || inventory == null) return false;
        if (amount <= 0) return false;

        if (amount == Integer.MAX_VALUE) {
            inventory.remove(itemStack);
            return true;
        }

        int toDelete = amount;
        if (inventory instanceof PlayerInventory) {
            PlayerInventory pInv = (PlayerInventory) inventory;
            ItemStack item = pInv.getItemInOffHand();
            if (item.isSimilar(itemStack))
                toDelete = removeItem(inventory, item, EquipmentSlot.OFF_HAND.ordinal(), toDelete);
        }

        if (toDelete <= 0) return true;
        for (int i = 0; i < inventory.getSize(); i++) {
            int first = inventory.first(itemStack);
            if (first == -1) return false;
            ItemStack item = inventory.getItem(first);
            assert item != null;
            toDelete = removeItem(inventory, item, first, toDelete);
            if (toDelete <= 0) {
                break;
            }
        }

        return true;
    }


    /**
     * Checks if two item stacks are equal.
     *
     * @param i1              First item.
     * @param i2              Second item.
     * @param checkDurability Whether to compare durability.
     * @return True if the items are equal, false otherwise.
     */
    public static boolean itemStackEquals(ItemStack i1, ItemStack i2, boolean checkDurability) {
        if (i1 == null || i2 == null) {
            return false;
        } else if (i1 == i2) {
            return true;
        } else {
            ItemMeta im1 = i1.getItemMeta();
            ItemMeta im2 = i2.getItemMeta();
            if (!checkDurability && MinecraftVersion.isNew()) {
                if (im1 instanceof org.bukkit.inventory.meta.Damageable) {
                    ((org.bukkit.inventory.meta.Damageable) im1).setDamage(0);
                }
                if (im2 instanceof org.bukkit.inventory.meta.Damageable) {
                    ((org.bukkit.inventory.meta.Damageable) im2).setDamage(0);
                }
            }
            return i1.getType() == i2.getType() && (!checkDurability || i1.getDurability() == i2.getDurability()) && i1.hasItemMeta() == i2.hasItemMeta() && (!i1.hasItemMeta() || Bukkit.getItemFactory().equals(im1, im2));
        }
    }

    /**
     * Util method for AManager#removeItems.
     */
    private static int removeItem(Inventory inventory, ItemStack itemStack, int slot, int toDelete) {
        if (itemStack.getAmount() <= toDelete) {
            toDelete -= itemStack.getAmount();
            /** This no longer handles OFF HAND correct. Slot 45 crashes the player client
             I recommend using removeItem(PlayerInventory, ItemStack, EquipmentSlot, int) instead - GC*/
            if (slot == 45 && inventory instanceof PlayerInventory)
                ((PlayerInventory) inventory).setItemInOffHand(null);
            else inventory.clear(slot);
        } else {
            itemStack.setAmount(itemStack.getAmount() - toDelete);
            inventory.setItem(slot, itemStack);
            toDelete = 0;
        }
        return toDelete;
    }

    /**
     * Util method for AManager#removeItems.
     */
    private static int removeItem(PlayerInventory inventory, ItemStack itemStack, EquipmentSlot slot, int toDelete) {
        if (itemStack.getAmount() <= toDelete) {
            toDelete -= itemStack.getAmount();
            inventory.setItem(slot, null);
        } else {
            itemStack.setAmount(itemStack.getAmount() - toDelete);
            inventory.setItem(slot, itemStack);
            toDelete = 0;
        }
        return toDelete;
    }

    /**
     * Gets an empty slot of a players inventory other than the slot specified (Used for DISARM
     *
     * @param slot   slot you don't want to get if its empty
     * @param player Players to use
     * @return -1 if a slot isn't found, slot number if one is found
     */
    public static int getEmptySlotOtherThan(final int slot, final Player player) {
        final PlayerInventory inventory = player.getInventory();
        int emptySlot = -1;
        List<Integer> dontIterate = Arrays.asList(36, 37, 38, 39, 40); // ignore armor slots and offhand
        for (int x = 0; x <= (player.getInventory().getSize() - 1); x++) {
            if (slot == x || dontIterate.contains(x)) continue;
            if (inventory.getItem(x) == null || inventory.getItem(x).getType() == Material.AIR) {
                emptySlot = x;
                return emptySlot;
            }
        }
        return emptySlot;
    }

    /**
     * Gives a player an item at a specific slot, this doesn't handle if said slot is not air or not use
     * getEmptySlotOtherThan before calling this method
     *
     * @param player    Player
     * @param itemStack ItemStack to add
     * @param slot      slot to use
     */
    public static void giveItemAtSlot(final Player player, ItemStack itemStack, final int slot) {
        if (!isValid(itemStack)) return;

        player.getInventory().setItem(slot, itemStack);
        player.updateInventory();
    }

    public static boolean hasPotionEffect(LivingEntity entity, PotionEffectType potionEffectType, int amplifier) {
        for (PotionEffect pe : entity.getActivePotionEffects()) {
            if (pe.getType() == potionEffectType && pe.getAmplifier() == amplifier) return true;
        }
        return false;
    }

    public static boolean isLog(Material material) {
        if (material != null && !isAir(material)) {
            boolean doStemsCount = instance.getConfig().getBoolean("settings.stems-count-as-trees", false);
            ;
            boolean isLog = material.name().endsWith("LOG") || material.name().endsWith("LOG_2");
            boolean isStem = material.name().endsWith("STEM");
            if (!isLog && !isStem) {
                return false;
            } else {
                return doStemsCount || !isStem;
            }
        } else {
            return false;
        }
    }

    public static String getOrDefault(Replace r, String def) {
        return r == null ? def : r.toString();
    }

    public static Object getOrDefault(Object obj, Object def) {
        return obj == null ? def : obj;
    }

    public static boolean doChancesPass(int chance) {
        return chance > ThreadLocalRandom.current().nextDouble() * 100;
    }

    public static void reduceHeldItems(Player p, EquipmentSlot slot, int byAmount) {
        ItemStack item = p.getInventory().getItem(slot);
        if (item.getAmount() - byAmount <= 0) item = null;
        else item.setAmount(item.getAmount() - byAmount);

        p.getInventory().setItem(slot, item);
    }

    public static String capitalize(String output) {
        output = output.replaceAll("_", " ").toLowerCase(Locale.ROOT);
        return output.substring(0, 1).toUpperCase() + output.substring(1);
    }

    public static String formatMaterialName(Material input) {
        return formatMaterialName(input.name());
    }

    public static String formatMaterialName(String input) {
        String output = input.toLowerCase().replaceAll("_", " ");
        output = capitalize(output);
        return output;
    }

    /**
     * Returns the obtainable item for a few blocks.
     *
     * @param material Block material.
     * @return Obtainable material.
     */
    public static Material getItemFromBlock(Material material) {
        if (MinecraftVersion.getVersionNumber() >= 1_12_0 && material.isItem()) return material;
        if (isWallBlock(material)) return getItemFromBlock(getItemFromBlock(material));

        switch (material.name()) {
            case "CARROTS":
                return Material.CARROT;
            case "COCOA":
                return Material.COCOA_BEANS;
            case "KELP_PLANT":
                return Material.KELP;
            case "POTATOES":
                return Material.POTATO;
            case "TRIPWIRE":
                return Material.STRING;
        }
        return material;
    }

    /**
     * Checks if the material provided needs to be attacked to the side of a block to exist.
     */
    public static boolean isWallBlock(Material material) {
        if (!isValid(material)) return false;
        String name = material.name();
        if (name.contains("SKULL") || name.contains("HEAD")) return false;
        return name.contains("WALL_") || name.equals("TRIPWIRE_HOOK") || name.equals("LADDER") || name.equals("LEVER") || name.contains("BUTTON") || name.contains("BANNER") || name.equals("COCOA");
    }

    public static Object extractFromDataArray(String array, String query, String split, Object defaultTo) {
        for (String s : array.split(" ")) {
            if (s.startsWith(query)) {
                return s.split(split)[1];
            }
        }
        return defaultTo;
    }


    public static String formatTime(long time) {
        int timeInSeconds = (int) (time / 1000L);
        int secondsLeft = timeInSeconds % 3600 % 60;
        int minutes = (int) Math.floor(timeInSeconds % 3600 / 60);
        int hours = (int) Math.floor(timeInSeconds / 3600);

        String f = "";
        if (hours > 0) {
            f = f + hours + "h ";
        }

        if (minutes > 0) {
            f = f + minutes + "m ";
        }

        f = f + secondsLeft + "s";
        return f;
    }

    public static void reportIssue(Exception ex, String addInfo) {
        StackTraceElement[] elements = ex.getStackTrace();
        String pckg = "";

        for (StackTraceElement element : elements) {
            String pack = element + "";
            if (pack.contains("net.advancedplugins.ae")) {
                pckg = pack;
                break;
            }
        }

        ex.printStackTrace();

        Bukkit.getLogger().info("[" + instance.getDescription().getName() + " ERROR] Could not pass " + ExceptionUtils.getRootCauseMessage(ex) + "");
        Bukkit.getLogger().info("   Class: " + pckg);
        Bukkit.getLogger().info("   Extra info: " + addInfo + "; mc[" + MinecraftVersion.getVersionNumber() + "];");
        Bukkit.getLogger().info("If you cannot indentify cause of this, contact developer providing this report. ");
    }

    public static Material getMaterial(String material) {
        try {
            return MinecraftVersion.getVersion().getVersionNumber() > 1121 ? Material.matchMaterial(material, true) : Material.matchMaterial(material);
        } catch (Exception ev) {
            ev.printStackTrace();
            //pashollll
            return null;
        }
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static List<String> replace(List<String> list, String from, String to) {
        list.replaceAll(line -> line.replace(from, to));
        return list;
    }

    public static int[] getSlots(String slotString) {
        int[] slots = new int[1];

        if (slotString.equalsIgnoreCase("filler")) {
            return slots;
        }

        if (slotString.contains(",")) {
            slots = Arrays.stream(slotString.split(",")).mapToInt(Integer::parseInt).toArray();
        } else if (slotString.contains("-")) {
            slots = Arrays.stream(slotString.split("-")).mapToInt(Integer::parseInt).toArray();
            slots = IntStream.rangeClosed(slots[0], slots[1]).toArray();
        } else {
            slots[0] = parseInt(slotString);
        }

        return slots;
    }

    public static boolean contains(String query, List<String> list) {
        for (String badWord : list) {
            if (query.toLowerCase(Locale.ROOT).contains(badWord.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String query, String[] list) {
        for (String badWord : list) {
            if (query.toLowerCase(Locale.ROOT).equalsIgnoreCase(badWord.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Turns a String to an int. Will ignore any non-numeric characters.
     *
     * @param toparse String to parse.
     * @return Int from the provided String.
     */
    public static int parseInt(String toparse) {
        return parseInt(toparse, 0);
    }

    public static int parseInt(String toparse, int def) {
        try {
            if (toparse.split("-").length > 1 && !toparse.substring(0, 1).equalsIgnoreCase("-")) {
                int min = Integer.parseInt(toparse.split("-")[0]);
                int max = Integer.parseInt(toparse.split("-")[1]);
                return ThreadLocalRandom.current().nextInt(max - min) + min;
            }
            return (int) Double.parseDouble(toparse.replaceAll("\"[^0-9.-]\"", "").replaceAll(" ", ""));
        } catch (Exception e) {
            instance.getLogger().warning("Failed to parse " + toparse + " from String to Integer.");
            e.printStackTrace();
            return def;
        }
    }

    // TODO this is broken!
    public static double parseDouble(String toparse, double def) {
        try {
            if (toparse.split("-").length > 1 && !toparse.substring(0, 1).equalsIgnoreCase("-")) {
                double min = Integer.parseInt(toparse.split("-")[0]);
                double max = Integer.parseInt(toparse.split("-")[1]);
                return ThreadLocalRandom.current().nextDouble(max - min) + min;
            }
            return Double.parseDouble(toparse.replaceAll("[^\\\\d.]", ""));
        } catch (Exception e) {
            instance.getLogger().warning("Failed to parse " + toparse + " from String to Double.");
            e.printStackTrace();
            return def;
        }
    }

    public static double round(double d, int decimalPlace) {
        return new BigDecimal(d).setScale(decimalPlace, RoundingMode.HALF_EVEN).doubleValue();

    }

    private static int findIfEnd(String syntax, int position) {
        if (position > syntax.length()) {
            return -1;
        }
        int end = syntax.indexOf("</if>", position);
        int possibleInner = syntax.indexOf("<if>", position);
        if (possibleInner > -1 && possibleInner < end) {
            int endOfInner = findIfEnd(syntax, possibleInner + 4);
            return findIfEnd(syntax, endOfInner + 5);
        }
        return end;
    }

    private static int findResultSplit(String syntax, int position) {
        if (position > syntax.length()) {
            return -1;
        }
        int split = syntax.indexOf(":", position);
        int possibleInner = syntax.indexOf("<if>", position);
        if (possibleInner > -1 && possibleInner < split) {
            int endOfInner = findIfEnd(syntax, possibleInner + 4);
            return findResultSplit(syntax, endOfInner + 1);
        }
        return split;
    }

    private static String[] splitAtIndex(String s, int idx) {
        if (idx >= s.length() - 1) {
            return new String[]{idx >= s.length() ? s : s.substring(0, idx)};
        }
        String s1 = s.substring(0, idx);
        String s2 = s.substring(idx + 1);
        return new String[]{s1, s2};
    }

    private static boolean checkStringsEquality(String condition) {
        if (condition.contains("===")) {
            String[] equalsElements = condition.split("===", 2);
            return equalsElements[0].equals(equalsElements[1]);
        }
        if (condition.contains("==")) {
            String[] equalsElements = condition.split("==", 2);
            return equalsElements[0].equalsIgnoreCase(equalsElements[1]);
        }
        return false;
    }

    private static String handleIfExpression(String syntax) {
        while (syntax.contains("<if>")) {
            int start = syntax.indexOf("<if>");
            int expressionStart = start + 4;
            int end = findIfEnd(syntax, expressionStart);
            String expression = syntax.substring(expressionStart, end);
            String[] elements = expression.split("\\?", 2);
            String condition = elements[0];
            int indexOfSplit = findResultSplit(elements[1], 0);
            String[] results = splitAtIndex(elements[1], indexOfSplit);

            boolean check = parseCondition(condition);
            String result = check ? results[0] : results[1];
            syntax = syntax.replace("<if>" + expression + "</if>", result);
        }
        return syntax;
    }

    public static boolean parseCondition(String condition) {
        condition = condition.replaceAll(" ", "");
        boolean check;
        Expression conditionMathExpression = new net.advancedplugins.utils.evalex.Expression(condition, MathContext.UNLIMITED);
        try {
            check = conditionMathExpression.eval().intValue() == 1;
        } catch (Exception e) {
            check = checkStringsEquality(condition);
        }
        return check;
    }

    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static double parseThroughCalculator(String syntax) {
        if (syntax.contains("<random>")) {
            String current = ASManager.substringBetween(syntax, "<random>", "</random>");
            int randomNum = parseInt(current);
            syntax = syntax.replace("<random>" + current + "</random>", Integer.toString(randomNum));
        }

        syntax = syntax.replaceAll(" ", "");
        syntax = handleIfExpression(syntax);

        Expression mathExpression = new net.advancedplugins.utils.evalex.Expression(syntax, MathContext.UNLIMITED);

        try {
            return mathExpression.eval().doubleValue();
        } catch (Exception ev) {
            ev.printStackTrace();
            Bukkit.getLogger().warning("Failed to calculate '" + syntax + "': Invalid syntax or outcome");
            return 0;
        }
    }

    public static void playEffect(String pe, float offSet, int amount, Location l) {
        if (MinecraftVersion.getVersion().getVersionNumber() < 1130) {
            try {
                Class<Enum> cls = ((Class<Enum>) Class.forName("org.bukkit.Effect"));
                Enum effect = Enum.valueOf((cls), pe);
                Method method = l.getWorld().spigot().getClass().getMethod("playEffect", Location.class, cls, int.class, int.class, float.class, float.class, float.class, float.class, int.class, int.class);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                method.invoke(l.getWorld().spigot(), l, effect, 0, 0, offSet, offSet, offSet, 0f, amount, 32);
            } catch (Exception e) {
            }
        } else {
            try {
                Class<Enum> cls = ((Class<Enum>) Class.forName("org.bukkit.Particle"));
                Enum particle = Enum.valueOf((cls), pe);
                Method method = l.getWorld().getClass().getMethod("spawnParticle", cls, Location.class, int.class, double.class, double.class, double.class, double.class);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                method.invoke(l.getWorld(), particle, l, amount, offSet, offSet, offSet, 0f);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    private static HashMap<Integer, String> damages = new HashMap<>();
    private static HashMap<String, String> newMaterials = new HashMap<>();

    static {
        if (damages.isEmpty()) {
            damages.put(0, "WHITE");
            damages.put(1, "ORANGE");
            damages.put(2, "MAGENTA");
            damages.put(3, "LIGHT_BLUE");
            damages.put(4, "YELLOW");
            damages.put(5, "LIME");
            damages.put(6, "PINK");
            damages.put(7, "GRAY");
            damages.put(8, "LIGHT_GRAY");
            damages.put(9, "CYAN");
            damages.put(10, "PURPLE");
            damages.put(11, "BLUE");
            damages.put(12, "BROWN");
            damages.put(13, "GREEN");
            damages.put(14, "RED");
            damages.put(15, "BLACK");
        }

        if (newMaterials.isEmpty()) {
            newMaterials.put("EYE_OF_ENDER", "ENDER_EYE");
            newMaterials.put("ENDER_PORTAL_FRAME", "END_PORTAL_FRAME");
            newMaterials.put("FIREWORK_CHARGE", "FIREWORK_STAR");
            newMaterials.put("FIREBALL", "FIRE_CHARGE");
            newMaterials.put("SULPHUR", "GUNPOWDER");
            newMaterials.put("WOOD_DOOR", "OAK_DOOR");
            newMaterials.put("COMMAND", "COMMAND_BLOCK");
            newMaterials.put("PISTON_BASE", "PISTON");
            newMaterials.put("SKULL_ITEM", "PLAYER_HEAD");
            newMaterials.put("WORKBENCH", "CRAFTING_TABLE");
            newMaterials.put("BOOK_AND_QUILL", "WRITABLE_BOOK");
            newMaterials.put("THIN_GLASS", "GLASS_PANE");
            newMaterials.put("STORAGE_MINECART", "CHEST_MINECART");
            newMaterials.put("BREWING_STAND_ITEM", "LEGACY_BREWING_STAND_ITEM");
        }
    }

    private static boolean startsWithColor(String input) {
        for (String color : damages.values()) {
            if (input.startsWith(color)) return true;
        }
        return false;
    }


    private static String addColor(String input, int damage) {
        String color = damages.get(damage);
        if (color == null) return input;

        return color + "_" + input;
    }

    private static boolean canAddColor(String input) {
        return input.contains("STAINED_GLASS") || input.contains("SHULKER") || input.contains("TERRACOTTA") || input.contains("WOOL") || (input.contains("BANNER") && !input.endsWith("BANNER_PATTERN")) || input.contains("DYE") || input.contains("CONCRETE") || input.contains("CARPET") || input.contains("BED");
    }

    public static ItemStack matchMaterial(String material, int amount, int damage) {
        return matchMaterial(material, amount, damage, false, true);
    }

    public static ItemStack matchMaterial(String material, int amount, int damage, boolean tryOld, boolean reportError) {
        boolean newVer = MinecraftVersion.getVersion().getVersionNumber() > 1121;
        if (newVer) {
            // Fix gold -> golden rename
            if (material.startsWith("GOLD_") && !material.contains("BLOCK") && !material.contains("NUGGET") && !material.contains("INGOT") && !material.contains("ORE")) {
                material = material.replace("GOLD_", "GOLDEN_");
            }

            // Resolve any incompatibilities with default config
            for (Map.Entry<String, String> materials : newMaterials.entrySet()) {
                if (materials.getKey().equalsIgnoreCase(material)) {
                    material = materials.getValue();
                    break;
                }
            }

            // Checks if this is a colored block (i.e. stained glass pane), since new materials
            // have different color structure instead of using damage
            if (canAddColor(material) && !startsWithColor(material)) {
                material = addColor(material, damage);
            }
        }

        try {
            // Re-tries material string to try getting legacy version of material, 2-step check to try give best support for materials possible
            Material m = tryOld ? Material.matchMaterial(material, true) : Material.matchMaterial(material);

            // Initiates ItemStack depending on version
            return !newVer ? new ItemStack(m, amount, (byte) damage) : new ItemStack(m, amount);
        } catch (Exception ev) {
            if (!tryOld && newVer) {
                return matchMaterial(material, amount, damage, true, reportError);
            }

            if (reportError) {
                Bukkit.getLogger().info("�cFailed to match '" + material + "' material, check your configuration or use https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html " + " to find needed material. �7�oFurther information has been pasted to console...");
                ev.printStackTrace();
            }
            return null;
        }
    }

    public static String getRoughNumber(long value) {
        if (value <= 999) {
            return String.valueOf(value);
        }

        final String[] units = new String[]{"", "K", "M", "B", "P"};
        int digitGroups = (int) (Math.log10(value) / Math.log10(1000));
        return new DecimalFormat("#,##0.#").format(value / Math.pow(1000, digitGroups)) + "" + units[digitGroups];

    }

    public static String format(long number) {
        return NumberFormat.getInstance(Locale.ROOT).format(number) + "";
    }

    public static String color(String input) {
        return ColorUtils.format(input);
    }

    /**
     * @return True if the material is 2 blocks tall when placed as a block, false otherwise.
     */
    public static boolean isTall(Material m) {
        if (m.name().endsWith("_DOOR")) return true;
        if (MinecraftVersion.isNew())
            return m == Material.SUNFLOWER || m == Material.LILAC || m == Material.ROSE_BUSH || m == Material.PEONY;
        else return m.name().equals("DOUBLE_PLANT");
    }

    public static List<Location> removeDuplicateLocations(List<Location> locations) {
        List<Location> uniqueLocations = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Location loc : locations) {
            if (loc == null || loc.getWorld() == null) continue;
            String key = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
            if (!seen.contains(key)) {
                seen.add(key);
                uniqueLocations.add(loc);
            }
        }
        return uniqueLocations;
    }

    /**
     * @return True if the material is not null and is not air.
     */
    public static boolean isValid(Material m) {
        return (m != null && !isAir(m));
    }

    /**
     * @return True if the item is not null, not air, and has more then 0 in the stack.
     */
    public static boolean isValid(ItemStack i) {
        return (i != null && i.getAmount() > 0 && !isAir(i.getType()));
    }

    /**
     * @return True if the block  is not null and is not air.
     */
    public static boolean isValid(Block b) {
        if (b == null || isAir(b.getType())) return false;
        String m = b.getType().name();
        if (m.endsWith("_PORTAL")) return false;
        if (m.contains("PISTON_")) return m.contains("PISTON_BASE") || m.contains("PISTON_STICKY_BASE");
        return !m.equals("FIRE") && !m.equals("SOUL_FIRE") && !m.equals("TALL_SEAGRASS") && !m.equals("SWEET_BERRY_BUSH") && !m.equals("BUBBLE_COLUMN") && !m.equals("LAVA");
    }

    public static void giveItem(Player p, ItemStack... items) {
        p.getInventory().setMaxStackSize(64);

        for (ItemStack item : items) {
            if (!isValid(item)) continue;
            if (!p.getInventory().addItem(item).isEmpty()) {
                if (!Bukkit.isPrimaryThread()) {
                    SchedulerUtils.runTaskLater(() -> dropItem(p.getLocation(), item));
                } else dropItem(p.getLocation(), item);
            }
        }
    }

    /**
     * Condenses multiple ItemStacks into as few as possible.
     *
     * @param itemStacks ItemStacks to condense.
     */
    public static List<ItemStack> condense(ItemStack[] itemStacks) {
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] == null) continue;

            for (int j = i + 1; j < itemStacks.length; j++) {
                if (itemStacks[j] == null) continue;
                if (itemStacks[i].isSimilar(itemStacks[j]) && itemStacks[i].getAmount() + itemStacks[j].getAmount() <= itemStacks[i].getMaxStackSize()) {
                    itemStacks[i].setAmount(itemStacks[i].getAmount() + itemStacks[j].getAmount());
                    itemStacks[j] = null;
                }
            }
        }
        return Arrays.stream(itemStacks).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Drops item at location if it's valid (not null, not air, more then 0).
     *
     * @param loc   Location to drop item at.
     * @param items Items to drop.
     */
    public static void dropItem(Location loc, ItemStack... items) {
        for (ItemStack i : items)
            if (i != null)
                loc.getWorld().dropItem(loc, i);
    }

    /**
     * @return True if the material is air.
     */
    public static boolean isAir(Material m) {
        if (m == null) return false;
        if (MinecraftVersion.getVersionNumber() >= 1_13_0) {
            return m == Material.AIR || m == Material.CAVE_AIR || m == Material.VOID_AIR || m == Material.LEGACY_AIR;
        }
        return m == Material.AIR;
    }

    /**
     * @return True if the block is air.
     */
    public static boolean isAir(Block b) {
        return b == null || isAir(b.getType());
    }

    /**
     * @return True if the item is air.
     */
    public static boolean isAir(ItemStack i) {
        return i == null || isAir(i.getType());
    }


    public static void sendActionBar(String message, Player p) {
        if (MinecraftVersion.getVersionNumber() >= 1_9_0) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else {
            String nmsVersion = "v1_8_R3";
            try {
                Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
                Object craftPlayer = craftPlayerClass.cast(p);

                Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
                Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
                Object packetPlayOutChat;
                Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
                Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

                Method method = null;
                if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

                Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
                packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

                Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
                Object iCraftPlayer = handle.invoke(craftPlayer);
                Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
                Object playerConnection = playerConnectionField.get(iCraftPlayer);
                Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
                sendPacket.invoke(playerConnection, packetPlayOutChat);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static int getPages(int size, int amountPerPage) {
        return ((int) size / amountPerPage) + (size % amountPerPage == 0 ? 0 : 1);
    }

    public static <T> List<T> getItemsInPage(List<T> items, int page, int itemsPerPage) {
        return items.subList(page * itemsPerPage, Math.min(items.size(), itemsPerPage * (page + 1)));
    }

    public static void deleteFile(File f) {
        if (f.isDirectory()) {
            for (File f2 : f.listFiles()) {
                deleteFile(f2);
            }
        }
        f.delete();
    }

    public static void unZip(File zip, File des) throws Exception {
        if (!des.exists() || !des.isDirectory()) des.mkdirs();
        ZipFile zipFile = new ZipFile(zip);
        ZipEntry entry = null;
        byte[] buffer = new byte[1024];
        Enumeration<? extends ZipEntry> enteries = zipFile.entries();
        try {
            while (enteries.hasMoreElements()) {
                entry = enteries.nextElement();
                if (entry.isDirectory()) {
                    File dir = new File(des, entry.getName());
                    dir.mkdirs();
                    continue;
                }
                InputStream fileStream = zipFile.getInputStream(entry);
                File file = new File(des, entry.getName());
                FileOutputStream outStream = new FileOutputStream(file);
                int read = 0;
                while ((read = fileStream.read(buffer)) > -1) outStream.write(buffer, 0, read);
                outStream.close();
                fileStream.close();
            }
        } finally {
            zipFile.close();
        }
    }

    /**
     * Gets the non-wall variant of a material. eg. WALL_TORCH -> TORCH.
     */
    public static Material getNonWallMaterial(Material material) {
        if (!isValid(material)) return material;
        String name = material.name();
        name = name.replace("WALL_", "");
        return Material.getMaterial(name);
    }

    /**
     * Gets the material fixed.
     * For example, CARROTS -> CARROT.
     *
     * @param material Material to fix.
     * @return Fixed material.
     */
    public static Material getFixedMaterial(Material material) {
        if (material == null) return null;

        switch (material.name()) {
            case "CARROTS": {
                if (MinecraftVersion.isNew()) {
                    return Material.CARROT;
                }
                return Material.matchMaterial("CARROT_ITEM");
            }

            case "POTATOES": {
                if (MinecraftVersion.isNew()) {
                    return Material.POTATO;
                }

                return Material.matchMaterial("POTATO_ITEM");
            }

            case "BEETROOTS": {
                return Material.BEETROOT;
            }

            case "COCOA": {
                return Material.COCOA_BEANS;
            }

            case "KELP_PLANT": {
                return Material.KELP;
            }

            case "TRIPWIRE": {
                return Material.STRING;
            }

            default:
                return material;
        }
    }

    /**
     * Checks if the provided material is effected by Fortune.
     *
     * @return True if the material is effected by Fortune, false otherwise.
     */
    public static boolean isFortuneBlock(Material m) {
        String typeName = m.name();
        boolean ore = typeName.endsWith("_ORE");
        boolean fortuneOnIronGold = false;
        boolean ironOrGold = ore && typeName.contains("GOLD") || typeName.contains("IRON");
        if (ore && !ironOrGold || (ore && ironOrGold && fortuneOnIronGold)) return true;
        switch (typeName) {
            case "SEEDS":
            case "WHEAT_SEEDS":
            case "GLOWSTONE":
            case "NETHER_WART":
            case "SWEET_BERRIES":
            case "SEA_LANTERN":
            case "NETHER_GOLD_ORE":
            case "MELON":
            case "MELON_BLOCK":
            case "AMETHYST_CLUSTER":
                return true;
        }
        return false;
    }

    /**
     * Gets the default amount of items dropped by a block.
     *
     * @param block Block to get drops from.
     * @return Default amount of items dropped.
     */
    public static int getDropAmount(Block block, Material dropType, ItemStack item) {
        Material blockType = block.getType();
        String typeName = blockType.name().replace("LEGACY_", "");

        boolean silk = item.getEnchantments().containsKey(Enchantment.SILK_TOUCH);
        int fortuneLevel = item.getEnchantmentLevel(VanillaEnchants.displayNameToEnchant("FORTUNE"));

        // If it's a block that requires Silk Touch and the item doesn't
        // have Silk Touch, return -1, so we know to skip it.
        boolean silkRequired = silkOnly.contains(typeName);
        if (silkRequired && !silk) return -1;

        boolean allowFortune = true;
        boolean fortuneBlock = ASManager.isFortuneBlock(blockType);

        int amount = 1;
        // Some blocks have a maximum amount they can drop even with stupid high levels of fortune, so we need to clamp the final amount with this.
        int max = Integer.MAX_VALUE;
        if (!silk) {
            switch (dropType.name()) {
                case "IRON_INGOT": // for SMELT
                case "RAW_IRON":
                case "GOLD_INGOT":
                case "RAW_GOLD": {
                    fortuneBlock = true;
                    allowFortune = true;
                    break;
                }
                case "ENDER_CHEST": {
                    fortuneBlock = false; // don't allow to multiply ender_chests
                    allowFortune = false;
                    break;
                }
                default:
                    break;
            }

            switch (typeName) {
                case "DEEPSLATE_COPPER_ORE":
                case "COPPER_ORE": {
                    amount = MathUtils.randomBetween(2, 5); // wiki says 2-5 not 2-3 or whatever this was before
                    break;
                }
                case "DEEPSLATE_LAPIS_ORE":
                case "LAPIS_ORE": {
                    amount = MathUtils.randomBetween(4, 9);
                    break;
                }
                case "SEA_LANTERN":
                    if (dropType == blockType) break;
                    max = 5;
                    break;
                case "DEEPSLATE_REDSTONE_ORE":
                case "REDSTONE_ORE": {
                    amount = MathUtils.randomBetween(4, 5);
                    break;
                }
                case "NETHER_GOLD_ORE": {
                    amount = MathUtils.randomBetween(2, 6);
                    break;
                }
                case "CLAY":
                case "AMETHYST_CLUSTER":
                    amount = 4;
                    break;
                case "MELON":
                case "MELON_BLOCK": {
                    amount = MathUtils.randomBetween(3, 7);
                    max = 9;
                    break;
                }
                case "GLOWSTONE": {
                    amount = MathUtils.randomBetween(2, 4);
                    max = 4;
                    break;
                }
                case "ENDER_CHEST": {
                    //amount = 1;
                    max = 1; // why were we dropping 8 ender_chests lmao
                    allowFortune = false;
                    fortuneBlock = false;
                    break;
                }
                case "BOOKSHELF": {
                    amount = 3;
                    max = 3;
                    allowFortune = false;
                    break;
                }

            }
        }

        if (CropUtils.isCrop(blockType)) {
            amount = CropUtils.getDropAmount(block, dropType, item);
        } else {
            boolean applyFortune = allowFortune && dropType != blockType && fortuneLevel > 0 && !silk && fortuneBlock;
            if (applyFortune) {
                if (blockType.name().endsWith("_ORE")) {
                    float r = ThreadLocalRandom.current().nextFloat();
                    if (r > (2.0f / (fortuneLevel + 2.0f))) {
                        final int previousAmount = amount;
                        for (int i = 0; i < previousAmount; i++) {
                            amount += new UniformIntegerDistribution(2, fortuneLevel + 1).sample();
                        }
                    }
                } else {
                    amount = new UniformIntegerDistribution(1, Math.min(max, amount * fortuneLevel)).sample();
                }
            }
        }

        // https://github.com/GC-spigot/AdvancedEnchantments/issues/4753
        if (blockType == Material.TURTLE_EGG) {
            TurtleEgg egg = (TurtleEgg) block.getBlockData();
            amount = egg.getEggs();
        }

        return MathUtils.clamp(amount, Integer.MIN_VALUE, max);
    }

    /**
     * Turns a Collection of Strings representing Materials into a Set of Materials.
     *
     * @param materials Collection of Material names.
     * @return A Set of Materials from the provided collection, excluding any Materials that don't exist.
     */
    public static Set<Material> createMaterialSet(Collection<String> materials) {
        return materials.stream().map(Material::matchMaterial).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static boolean isUnbreakable(ItemStack itemStack) {
        return (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().isUnbreakable()) || NBTapi.contains("Unbreakable", itemStack);
    }

    public static Object getNMSEntity(LivingEntity entity) {
        return ReflectionMethod.CRAFT_ENTITY_GET_HANDLE.run(ClassWrapper.CRAFT_ENTITY.getClazz().cast(entity));
    }

    /**
     * Checks if a Material is damageable.
     * I'm not sure if this is 100% accurate, but it seems to work.
     *
     * @param material Material to check.
     * @return True if the material is damageable, false otherwise.
     */
    public static boolean isDamageable(Material material) {
        return material.getMaxDurability() > 0;
    }


    public static String tryOrElse(TryCatchMethodShort s, String rt) {
        try {
            return s.tryCatch();
        } catch (Exception ev) {
            //ev.printStackTrace();
            return rt;
        }
    }

    public static void setByMatching(ItemStack compareTo, ItemStack item, LivingEntity ent) {
        if (compareTo.isSimilar(ent.getEquipment().getItemInMainHand())) {
            ent.getEquipment().setItemInMainHand(item);
            return;
        }

        if (compareTo.isSimilar(ent.getEquipment().getItemInOffHand())) ent.getEquipment().setItemInOffHand(item);
    }

    /**
     * Checks if a player has a Totem of Undying.
     *
     * @param player Player to check.
     * @return True if the player has a totem of undying, false otherwise.
     */
    public static boolean hasTotem(Player player) {
        ItemStack offHand = player.getInventory().getItemInOffHand();
        return isValid(offHand) && offHand.getType() == Material.TOTEM_OF_UNDYING;
    }

    /**
     * Sets the player's health to the health provided one server tick later.
     *
     * @param player Player to set health for.
     * @param health Health to set.
     */
    public static void resetPlayerHealth(Player player, double health) {
//        SchedulerUtils.runTaskLater(() -> {
        double newHealth = MathUtils.clamp(health, player.getHealth(), player.getMaxHealth());
        player.setHealth(newHealth);
//        }, 1);
    }

    public static String getBlockMaterial(Block clickedBlock) {
        if (clickedBlock == null) return "AIR";
        return clickedBlock.getType().name();
    }

    public static List<String> getVariables(String input, String start, String end) {
        List<String> rt = new ArrayList<>();
        int now = 0;
        for (String split : input.split(start)) {
            now++;
            if (now == 1) continue;
            rt.add(split.split(end)[0]);
        }
        return rt;
    }

    /**
     * Checks if a String representing an Enum is valid.
     *
     * @param enumType Type of enum.
     * @param name     Name to check.
     * @return True if the String is a valid enum, false otherwise.
     */
    public static <T extends Enum<T>> boolean isValidEnum(Class<T> enumType, String name) {
        try {
            Enum.valueOf(enumType, name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if an Item is the correct tool to break a block.
     *
     * @param tool      Tool to test.
     * @param blockType Type of block to test.
     * @return True if the provided tool is the correct one, false otherwise.
     */
    public static boolean isCorrectTool(ItemStack tool, Material blockType) {
        Object item = ReflectionMethod.CRAFT_ItemStack_asNMSCopy.run(null, tool);
        Object block = ReflectionMethod.CRAFT_MagicNumbers_getBlock.run(null, blockType);
        Object data = ReflectionMethod.NMS_Block_getBlockData.run(block);
        return (boolean) ReflectionMethod.NMS_ItemStack_canDestroySpecialBlock.run(item, data);
    }

    public static boolean notNullAndTrue(Boolean value) {
        if (value == null) return false;
        return value;
    }

    public static boolean sameBlock(Location locationOne, Location locationTwo) {
        return locationOne.getBlockX() == locationTwo.getBlockX() && locationOne.getBlockY() == locationTwo.getBlockY() && locationOne.getBlockZ() == locationTwo.getBlockZ();
    }

    public static boolean debug = false;

    public static void debug(String string) {
        if (!debug) return;
        Bukkit.getLogger().info(string);
        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("advancedplugins.admin") || player.isOp()).forEach(player -> player.sendMessage(ColorUtils.format(string)));
    }

    public static String join(String[] args, String s) {
        return join(Arrays.asList(args), s);
    }

    public static String join(Collection<String> args, String s) {
        if (args.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (String l : args) {
            builder.append(capitalize(l)).append(s);
        }
        return builder.substring(0, builder.length() - s.length());
    }

    public static String join(Iterable<String> args, String s) {
        StringBuilder builder = new StringBuilder();
        for (String l : args) {
            builder.append(l).append(s);
        }
        return builder.substring(0, builder.length() - s.length());
    }

    public static String getMaterial(ItemStack itemStack) {
        if (itemStack == null) return "AIR";
        return itemStack.getType().name();
    }

    public static <T> T getFromArray(T[] split, int pos) {
        // if pos = -1, assume it's the last one
        if (pos == -1) pos = split.length - 1;
        return split[pos];
    }

    public static String limit(String value, int i, String endWith) {
        return value.length() < i ? value : value.substring(0, i - 1) + endWith;
    }

    public static String join(String[] split, String s, int from, int to) {
        StringBuilder builder = new StringBuilder();
        to = Math.max(split.length, to);
        for (int i = from; i < to; i++) {
            builder.append(split[i]).append(s);
        }
        return builder.substring(0, builder.length() - s.length());
    }

    public static int getEmptySlotCountInInventory(@NotNull Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            return 0;
        }
        int emptySlots = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType().equals(Material.AIR)) {
                emptySlots++;
            }
        }
        return emptySlots;
    }

    public static <K, V> ImmutableMap<K, V> toImmutable(Map<K, V> data) {
        return ImmutableMap.<K, V>builder().putAll(data).build();
    }

    public static <V> ImmutableList<V> toImmutableList(List<V> list) {
        return new ImmutableList.Builder<V>().addAll(list).build();
    }

    private static final ImmutableList<String> vegetationBlockNames = ImmutableList.<String>builder().addAll((Arrays.asList("GRASS", "TALL_GRASS", "FERN", "LARGE_FERN", "SEAGRASS", "TALL_SEAGRASS", "DANDELION", "POPPY", "BLUE_ORCHID", "ALLIUM", "AZURE_BLUET", "RED_TULIP", "ORANGE_TULIP", "WHITE_TULIP", "PINK_TULIP", "OXEYE_DAISY", "CORNFLOWER", "LILY_OF_THE_VALLEY", "WITHER_ROSE", "SUNFLOWER", "LILAC", "ROSE_BUSH", "PEONY"))).build();


    public static boolean isVegetation(Material type) {
        return vegetationBlockNames.contains(type.name());
    }

    public static <V> List<String> toStringList(V... values) {
        List<String> list = new ArrayList<>();
        for (V v : values) {
            list.add(v.toString());
        }
        return list;
    }

    public static int[] subarray(int[] array, int from, int to) {
        if (array == null || from < 0 || to > array.length || from > to) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        return Arrays.copyOfRange(array, from, to);
    }

    public static <V> V[] subarray(V[] array, int from, int to) {
        if (array == null || from < 0 || to > array.length || from > to) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        return Arrays.copyOfRange(array, from, to);
    }

    public static boolean isDay(long time) {
        return time > 0 && time < 12300;
    }

    public static void fillEmptyInventorySlots(Inventory inventory, ItemStack itemStack) {
        IntStream.range(0, inventory.getSize()).filter(slot -> inventory.getItem(slot) == null).forEach(slot -> inventory.setItem(slot, itemStack));
    }

    public static Location offsetToLookingLocation(Location loc, double distance) {
        Location newLoc = loc.clone();
        Vector direction = newLoc.getDirection();
        direction.normalize();
        direction.multiply(distance);
        newLoc.add(direction);
        return newLoc;
    }

    public static <K, V> ImmutableMap<K, V> configToImmutableMap(FileConfiguration config, String section, Function<String, K> keyTransformer, Class<V> valueType) {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        for (String key : config.getConfigurationSection(section).getKeys(false)) {
            V value = valueType.cast(config.get(section + "." + key));
            builder.put(keyTransformer.apply(key.toUpperCase(Locale.ROOT)), value);
        }
        return builder.build();
    }

    public static boolean isHostile(EntityType type) {
        return Monster.class.isAssignableFrom(type.getEntityClass());
    }

    public static Map<String, String> stringToMap(String... s) {
        Map<String, String> map = new HashMap<>();
        for (String s1 : s) {
            String[] split = s1.split(";");
            if (split.length == 2)
                map.put(split[0], split[1]);
        }
        return map;
    }

    public static <T> ImmutableMap<String, T> configObjecstToImmutableMap(Class<T> clazz, FileConfiguration config, String path) {
        ImmutableMap.Builder<String, T> immutableMap = ImmutableMap.builder();

        Set<String> keys = config.getConfigurationSection(path).getKeys(false);
        for (String key : keys) {
            try {
                T instance = clazz.getDeclaredConstructor().newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);

                    ConfigKey configKey = field.getAnnotation(ConfigKey.class);
                    if (configKey == null) {
//                        log("Missing ConfigKey annotation for field " + field.getName() + " in class " + clazz.getSimpleName());
                        continue;
                    }

                    Object value;
                    // Empty configKey means it should bet set as the key
                    if (configKey.value().isEmpty()) {
                        value = key;
                    } else {
                        value = config.get(path + "." + key + "." + configKey.value());
                    }

                    field.set(instance, value);
                }
                immutableMap.put(key, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return immutableMap.build();
    }

    public static void log(String text) {
        if (!Registry.get().equalsIgnoreCase("9454"))
            return;
        instance.getLogger().warning(Text.modify("&c&o[DEV DEBUG]&r " + text));
        Bukkit.broadcastMessage(Text.modify("&c&o[DEV DEBUG]&r " + text));
    }

    public static FileConfiguration loadConfig(File file) {
        try {
            return YamlConfiguration.loadConfiguration(file);
        } catch (Exception ev) {
            getInstance().getLogger().severe("Failed to load " + file.getName() + " file, check your configuration and try again.");
            return null;
        }
    }

    public static ItemStack makeItemGlow(ItemStack itemstack, @Nullable Boolean glow) {
        // 1.20.5 added a proper way for ench glow
        if (itemstack.hasItemMeta() && MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
            try {
                ItemMeta meta = itemstack.getItemMeta();
                meta.setEnchantmentGlintOverride(glow);
                itemstack.setItemMeta(meta);
            } catch (Exception ev) {
                ev.printStackTrace();
            }
        }
        return itemstack;
    }

    public static ItemStack makeItemGlow(ItemStack itemstack) {
        return makeItemGlow(itemstack, true);
    }

    public static Pair<String, Integer> parseEnchantment(String ench) {
        String[] parsed = ench.split(":");

        if (parsed[0].startsWith("!")) {
            int chance = Integer.parseInt(parsed[0].replace("!", ""));
            if (ThreadLocalRandom.current().nextInt(100) + 1 > chance) {
                return null;
            }
            parsed = new String[]{parsed[1], parsed[2]};
        }

        int level;
        if (parsed[1].contains("%")) {
            String[] sr = parsed[1].replace("%", "").split("-");
            int min = Integer.parseInt(sr[0]);
            int max = Integer.parseInt(sr[1]);
            level = MathUtils.randomBetween(min, max);
        } else {
            level = Integer.parseInt(parsed[1]);
        }

        String enchStr = parsed[0];
        return new Pair<>(enchStr, level);
    }

    public static void saveResource(String s) {
        if (new File(instance.getDataFolder(), s).isFile()) return;
        getInstance().saveResource(s, false);
    }

    public static boolean parseBoolean(String arg, boolean b) {
        if (arg == null) return b;
        if (arg.equalsIgnoreCase("true")) return true;
        if (arg.equalsIgnoreCase("false")) return false;
        return b;
    }

    public static int minmax(int base, int min, int max) {
        return Math.max(min, Math.min(max, base));
    }

    public static String fetchJsonFromUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    public static <T> T randomElement(Collection<T> values) {
        if (values.isEmpty()) {
            return null;
        }

        final int randomIndex = ThreadLocalRandom.current().nextInt(values.size());

        if (values instanceof List) {
            // Lists support fast random access.
            List<T> list = (List<T>) values;
            return list.get(randomIndex);
        } else {
            // For sets and other collections, use an iterator to reach the random element.
            Iterator<T> iterator = values.iterator();
            for (int i = 0; i < randomIndex; i++) {
                iterator.next();
            }
            return iterator.next();
        }
    }

    public static <T> List<T> reverse(Set<T> formats) {
        List<T> list = new ArrayList<>(formats);
        Collections.reverse(list);
        return list;
    }

    public static boolean isOnline(LivingEntity ent) {
        if (!(ent instanceof Player)) return true;
        return ((Player) ent).isOnline();
    }

    public static String[] listFiles(String folder) {
        return new File(instance.getDataFolder(), folder).list();
    }

    public static File getFile(String s) {
        return new File(instance.getDataFolder(), s);
    }

    public static String join(Map map, String format, int maxLineLength) {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        for (Object loopItem : map.entrySet()) {
            Map.Entry entry = (Map.Entry) loopItem;
            builder.append(format.replace("%k%", capitalize(entry.getKey().toString())).replace("%v%", entry.getValue().toString()));

            // If the line is too long, move to a new line
            if (builder.length() > maxLineLength * i) {
                builder.append("\n");
                i++;
            }
        }
        return builder.toString();
    }

    public static boolean isPlayer(Entity ent) {
        return ent instanceof Player;
    }

    public static int[] getNumbersInRange(int i, int end) {
        int[] rt = new int[end - i];
        for (int j = i; j < end; j++) {
            rt[j - i] = j;
        }
        return rt;
    }

    public static BlockFace getCardinalDirection(float yaw) {
        if (yaw < 0) {
            yaw += 360;
        }
        yaw = yaw % 360;
        if (yaw <= 45) {
            return BlockFace.NORTH;
        } else if (yaw <= 135) {
            return BlockFace.EAST;
        } else if (yaw <= 225) {
            return BlockFace.SOUTH;
        } else if (yaw <= 315) {
            return BlockFace.WEST;
        } else {
            return BlockFace.NORTH;
        }
    }

    public static Collection<Block> getNearbyBlocks(Location location, float x, float y, float z) {
        Collection<Block> blocks = new ArrayList<>();
        for (float i = -x; i <= x; i++) {
            for (float j = -y; j <= y; j++) {
                for (float k = -z; k <= z; k++) {
                    blocks.add(location.clone().add(i, j, k).getBlock());
                }
            }
        }
        return blocks;
    }
}
