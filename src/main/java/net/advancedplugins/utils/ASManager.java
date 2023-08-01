package net.advancedplugins.utils;

import net.advancedplugins.utils.evalex.Expression;
import net.advancedplugins.utils.nbt.NBTapi;
import net.advancedplugins.utils.nbt.backend.ClassWrapper;
import net.advancedplugins.utils.nbt.backend.ReflectionMethod;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.utils.text.Replace;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ASManager {
    private static final HashSet<String> silkOnly = new HashSet<>(Arrays.asList("LEAVE", "LEAVES", "MUSHROOM_STEM", "TURTLE_EGG", "CORAL"));

    private static JavaPlugin instance;

    public static JavaPlugin getInstance() {
        return instance;
    }

    public static void setInstance(JavaPlugin instance) {
        ASManager.instance = instance;
    }

    private static final List<Integer> validSizes = new ArrayList<>(Arrays.asList(9, 18, 27, 36, 45, 54));

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
        if (b == null || b.getType() == null)
            return false;
        return isSpawner(b.getType());
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
        if (radius < 1)
            return (radius == 0) ? Collections.singletonList(start) : Collections.emptyList();
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
            if (item == null)
                continue;
            if (item.getType() != material)
                continue;

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
            if (item == null)
                continue;

            if (item.getType() != m)
                continue;

            count -= item.getAmount();
            if (count <= 0)
                return true;
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
        if (material == null || inventory == null)
            return false;
        if (amount <= 0)
            return false;

        if (amount == Integer.MAX_VALUE) {
            inventory.remove(material);
            return true;
        }

        int toDelete = amount;
        if (MinecraftVersion.getVersionNumber() >= 1_9_0 && inventory instanceof PlayerInventory) {
            PlayerInventory pInv = (PlayerInventory) inventory;
            ItemStack item = pInv.getItemInOffHand();
            if (item.getType() == material)
                toDelete = removeItem(inventory, item, 45, toDelete);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            int first = inventory.first(material);
            if (first == -1)
                return false;
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
        if (itemStack == null || inventory == null)
            return false;
        if (amount <= 0)
            return false;

        if (amount == Integer.MAX_VALUE) {
            inventory.remove(itemStack);
            return true;
        }

        int toDelete = amount;
        if (MinecraftVersion.getVersionNumber() >= 1_9_0 && inventory instanceof PlayerInventory) {
            PlayerInventory pInv = (PlayerInventory) inventory;
            ItemStack item = pInv.getItemInOffHand();
            if (item.isSimilar(itemStack))
                toDelete = removeItem(inventory, item, 45, toDelete);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            int first = inventory.first(itemStack);
            if (first == -1)
                return false;
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
            if (slot == 45 && inventory instanceof PlayerInventory)
                ((PlayerInventory) inventory).setItemInOffHand(null);
            else
                inventory.clear(slot);
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
        if (!isValid(itemStack))
            return;

        player.getInventory().setItem(slot, itemStack);
        player.updateInventory();
    }

    public static boolean hasPotionEffect(LivingEntity entity, PotionEffectType potionEffectType, int amplifier) {
        for (PotionEffect pe : entity.getActivePotionEffects()) {
            if (pe.getType() == potionEffectType && pe.getAmplifier() == amplifier)
                return true;
        }
        return false;
    }

    public static boolean isLog(Material material) {
        if (material != null && !isAir(material)) {
            boolean doStemsCount = false;
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

    public static boolean doChancesPass(int chance) {
        return chance > ThreadLocalRandom.current().nextDouble() * 100;
    }

    public static void reduceHeldItems(Player p, EquipmentSlot slot, int byAmount) {
        ItemStack item = p.getInventory().getItem(slot);
        if (item.getAmount() - byAmount <= 0)
            item = null;
        else
            item.setAmount(item.getAmount() - byAmount);

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
        String output = input
                .toLowerCase()
                .replaceAll("_", " ");
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
        if (isWallBlock(material))
            return getItemFromBlock(getItemFromBlock(material));

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
                return Material.TRIPWIRE_HOOK;
        }
        return material;
    }

    /**
     * Checks if the material provided needs to be attacked to the side of a block to exist.
     */
    public static boolean isWallBlock(Material material) {
        if (!isValid(material)) return false;
        String name = material.name();
        if (name.contains("SKULL") || name.contains("HEAD"))
            return false;
        return name.contains("WALL_") || name.equals("TRIPWIRE_HOOK") || name.equals("LADDER") || name.equals("LEVER")
                || name.contains("BUTTON") || name.contains("BANNER") || name.equals("COCOA");
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

    public static List<String> replace(List<String> list, String from, String to) {
        list.replaceAll(line -> line.replace(from, to));
        return list;
    }

    public static int[] getSlots(String slotString) {
        int[] slots = new int[1];

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
            return (int) Double.parseDouble(toparse.replaceAll("\"[^0-9.-]\"", "")
                    .replaceAll(" ", ""));
        } catch (Exception e) {
            instance.getLogger().warning("Failed to parse " + toparse + " from String to Integer.");
            e.printStackTrace();
            return def;
        }
    }

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

    public static double parseThroughCalculator(String syntax) {
        if (syntax.contains("<random>")) {
            String current = StringUtils.substringBetween(syntax, "<random>", "</random>");
            int randomNum = parseInt(current);
            syntax = syntax.replace("<random>" + current + "</random>", Integer.toString(randomNum));
        }

        syntax = syntax.replaceAll(" ", "");
        Expression mathExpression = new Expression(syntax);

        try {
            return mathExpression.eval().doubleValue();
        } catch (Exception ev) {
            ev.printStackTrace();
            Bukkit.getLogger().warning("Failed to calculate '" + syntax + "': Invalid syntax or outcome");
            return 0;
        }
    }

    public static void playEffect(String pe, int offSet, int amount, Location l) {
        if (MinecraftVersion.getVersion().getVersionNumber() < 1130) {
            try {
                Class<Enum> cls = ((Class<Enum>) Class.forName("org.bukkit.Effect"));
                Enum effect = Enum.valueOf((cls), pe);
                Method method = l.getWorld().spigot().getClass().getMethod("playEffect", Location.class,
                        cls,
                        int.class, int.class, float.class, float.class, float.class, float.class, int.class, int.class);

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
                Method method = l.getWorld().getClass().getMethod("spawnParticle", cls, Location.class,
                        int.class, double.class, double.class, double.class, double.class);

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
            if (input.startsWith(color))
                return true;
        }
        return false;
    }


    private static String addColor(String input, int damage) {
        String color = damages.get(damage);
        if (color == null)
            return input;

        return color + "_" + input;
    }

    private static boolean canAddColor(String input) {
        return input.contains("STAINED_GLASS") ||
                input.contains("SHULKER") ||
                input.contains("TERRACOTTA") ||
                input.contains("WOOL") ||
                input.contains("BANNER") ||
                input.contains("DYE") ||
                input.contains("CONCRETE") ||
                input.contains("CARPET") ||
                input.contains("BED");
    }

    public static ItemStack matchMaterial(String material, int amount, int damage) {
        return matchMaterial(material, amount, damage, false, true);
    }

    public static ItemStack matchMaterial(String material, int amount, int damage, boolean tryOld, boolean reportError) {
        boolean newVer = MinecraftVersion.getVersion().getVersionNumber() > 1121;
        if (newVer) {
            // Fix gold -> golden rename
            if (material.startsWith("GOLD_")
                    && !material.contains("BLOCK")
                    && !material.contains("NUGGET")
                    && !material.contains("INGOT")
                    && !material.contains("ORE")) {
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
                Bukkit.getLogger().info("�cFailed to match '" + material + "' material, check your configuration or use materials.txt " +
                        " to find needed material. �7�oFurther information has been pasted to console...");
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
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     * @return True if the material is 2 blocks tall when placed as a block, false otherwise.
     */
    public static boolean isTall(Material m) {
        if (m.name().endsWith("_DOOR"))
            return true;
        if (MinecraftVersion.isNew())
            return m == Material.SUNFLOWER || m == Material.LILAC || m == Material.ROSE_BUSH || m == Material.PEONY;
        else
            return m == Material.valueOf("DOUBLE_PLANT");
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
     * @return True if the block is not null and is not air.
     */
    public static boolean isValid(Block b) {
        if (b == null || isAir(b.getType())) return false;
        String m = b.getType().name();
        if (m.endsWith("_PORTAL"))
            return false;
        if (m.contains("PISTON_"))
            return m.contains("PISTON_BASE") || m.contains("PISTON_STICKY_BASE");
        return !m.equals("FIRE") && !m.equals("SOUL_FIRE") && !m.equals("TALL_SEAGRASS") && !m.equals("SWEET_BERRY_BUSH") && !m.equals("BUBBLE_COLUMN") && !m.equals("LAVA");
    }

    public static void giveItem(Player p, ItemStack... items) {
        for (ItemStack item : items) {
            if (!isValid(item)) continue;
            if (!p.getInventory().addItem(item).isEmpty()) {
                dropItem(p.getLocation(), item);
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
        for (ItemStack i : items) loc.getWorld().dropItem(loc, i);
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
                Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion
                        + ".entity.CraftPlayer");
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

    public static void deleteFile(File f) {
        if (f.isDirectory()) {
            for (File f2 : f.listFiles()) {
                deleteFile(f2);
            }
        }
        f.delete();
    }

    public static void unZip(File zip, File des) throws Exception {
        if (!des.exists() || !des.isDirectory())
            des.mkdirs();
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
                while ((read = fileStream.read(buffer)) > -1)
                    outStream.write(buffer, 0, read);
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
                return Material.TRIPWIRE_HOOK;
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
        if (ore && !ironOrGold || (ore && ironOrGold && fortuneOnIronGold))
            return true;
        switch (typeName) {
            case "SEEDS":
            case "WHEAT_SEEDS":
            case "GLOWSTONE":
            case "NETHER_WART":
            case "SWEET_BERRIES":
            case "SEA_LANTERN":
            case "MELON":
            case "MELON_BLOCK":
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
        int fortuneLevel = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

        // If it's a block that requires Silk Touch and the item doesn't
        // have Silk Touch, return -1, so we know to skip it.
        boolean silkRequired = silkOnly.contains(typeName);
        if (silkRequired && !silk)
            return -1;

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
                    if (dropType == blockType)
                        break;
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
                        amount = new UniformIntegerDistribution(2, fortuneLevel + 1).sample();
                    }
                } else {
                    amount = new UniformIntegerDistribution(1, Math.min(max, amount * fortuneLevel)).sample();
                }
            }
        }

        return MathUtils.clamp(amount, Integer.MIN_VALUE, max);
    }

    public static boolean isUnbreakable(ItemStack itemStack) {
        return NBTapi.contains("Unbreakable", itemStack);
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

        if (compareTo.isSimilar(ent.getEquipment().getItemInOffHand()))
            ent.getEquipment().setItemInOffHand(item);
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
        if (clickedBlock == null)
            return "AIR";
        return clickedBlock.getType().name();
    }

    public static List<String> getVariables(String input, String start, String end) {
        List<String> rt = new ArrayList<>();
        int now = 0;
        for (String split : input.split(start)) {
            now++;
            if (now == 1)
                continue;
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
//        if (MinecraftVersion.getVersionNumber() == 1_17_0) {
//            IBlockData data = CraftMagicNumbers.getBlock(blockType).getBlockData();
//            return CraftItemStack.asNMSCopy(tool).canDestroySpecialBlock(data);
//        } else if (MinecraftVersion.getVersionNumber() >= 1_18_0) {
//            IBlockData data = org.bukkit.craftbukkit.v1_17_R1.getBlock(blockType).getBlockData();
//            return CraftItemStack.asNMSCopy(tool).canDestroySpecialBlock(data);
////        } else {
        // code below was a test to remove NMS, didn't work
//        if(MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_18_R1)) {
//            Bukkit.broadcastMessage("passedBlock.getBreakSpeed(p) "+passedBlock.getBreakSpeed(p) +" "+blockType.name());
//            return passedBlock.getBreakSpeed(p) > 1;
//        }

        Object item = ReflectionMethod.CRAFT_ItemStack_asNMSCopy.run(null, tool);
        Object block = ReflectionMethod.CRAFT_MagicNumbers_getBlock.run(null, blockType);
        Object data = ReflectionMethod.NMS_Block_getBlockData.run(block);
        return (boolean) ReflectionMethod.NMS_ItemStack_canDestroySpecialBlock.run(item, data);
        //  }
    }

    public static boolean notNullAndTrue(Boolean value) {
        if (value == null)
            return false;
        return value;
    }

    public static boolean sameBlock(Location locationOne, Location locationTwo) {
        return locationOne.getBlockX() == locationTwo.getBlockX()
                && locationOne.getBlockY() == locationTwo.getBlockY()
                && locationOne.getBlockZ() == locationTwo.getBlockZ();
    }

    public static boolean debug = false;

    public static void debug(String string) {
        if (!debug) return;
        Bukkit.getLogger().info(string);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("advancedplugins.admin") || player.isOp())
                .forEach(player -> player.sendMessage(ColorUtils.format(string)));
    }

    public static String join(String[] args, String s) {
        return join(Arrays.asList(args), s);
    }

    public static String join(Collection<String> args, String s) {
        StringBuilder builder = new StringBuilder();
        for (String l : args) {
            builder.append(l).append(s);
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
        if (itemStack == null)
            return "AIR";
        return itemStack.getType().name();
    }

    public static <T> T getFromArray(T[] split, int pos) {
        // if pos = -1, assume it's the last one
        if (pos == -1)
            pos = split.length - 1;
        return split[pos];
    }

    public static String limit(String value, int i, String endWith) {
        return value.length() < i ? value : value.substring(0, i - 1) + endWith;
    }
}
