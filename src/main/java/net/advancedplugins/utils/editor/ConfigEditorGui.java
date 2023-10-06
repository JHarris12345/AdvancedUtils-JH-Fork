package net.advancedplugins.utils.editor;

import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.ColorUtils;
import net.advancedplugins.utils.SchedulerUtils;
import net.advancedplugins.utils.items.ItemBuilder;
import net.advancedplugins.utils.nbt.NBTapi;
import net.advancedplugins.utils.text.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigEditorGui implements Listener {

    private final JavaPlugin plugin;

    private final Player editor;

    private String defaultDesc = "";
    private String defaultMaterial = "PAPER";

    private final ConfigurationSection config;
    private final String baseSection;

    private String currentSection = "";
    private String lastCurrentSection = "";

    private String editingKey;
    private final KeyInfo editObject;
    private final LinkedList<KeyInfo> keyInfos = new LinkedList<>();

    // Inventory
    private final Inventory inventory;
    private int page = 0;
    private static final int ITEMS_PER_PAGE = 27;

    private boolean enabled = true;

    public ConfigEditorGui(String name, String baseSection, ConfigurationSection config, Player player, LinkedList<KeyInfo> keyInfos, KeyInfo editObject, JavaPlugin plugin) {
        this.config = config;

        // This will be the base for this editor, it cannot go back beyond this path.
        this.baseSection = baseSection; //since that's the base for ConfigSection // config.getCurrentPath();

        this.keyInfos.addAll(keyInfos);
        this.editor = player;
        this.plugin = plugin;
        this.editObject = editObject;
        this.inventory = Bukkit.createInventory(null, 18, Text.modify(name));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public ConfigEditorGui open() {
        if (!lastCurrentSection.equalsIgnoreCase(currentSection)) {
            lastCurrentSection = currentSection;
            page = 0;
        }

        inventory.clear();

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, config.getConfigurationSection(currentSection).getKeys(false).size());
        List<String> keys = new ArrayList<>(config.getConfigurationSection(currentSection).getKeys(false)).subList(start, end);

        for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemBuilder(Material.matchMaterial(ConfigEditorMenu.getHandler().getGlassColor() + "_STAINED_GLASS_PANE"))
                    .setName(" ").setGlowing(true).toItemStack());
        }

        for (String key : keys) {
            key = getPathWithKey(key);
            KeyInfo info = matchInfo(key);

            inventory.addItem(getItemForKey(key, info));
        }

        for (KeyInfo info : getNotSetKeys()) {
            if (info.path.endsWith("*") || keys.contains(getLastKey(info.path)))
                continue;
            if (config.isConfigurationSection(info.path) || config.contains(info.path))
                continue;
            inventory.addItem(getItemForKey(info.path, info));
        }

        if (matchInfo(currentSection + ".*") != null) {
            inventory.addItem(new ItemBuilder(Material.GREEN_WOOL)
                    .setName(Text.modify("&a&lAdd new entry."))
                    .toItemStack());
        }

        if (page > 0) {
            ItemBuilder prevButton = new ItemBuilder(Material.BOOK).setAmount(page).setName("Previous Page");
            inventory.setItem(inventory.getSize() - 6, prevButton.toItemStack());
        }
        if (end < keys.size()) {
            ItemBuilder nextButton = new ItemBuilder(Material.BOOK).setAmount(page + 2).setName("Next Page");
            inventory.setItem(inventory.getSize() - 4, nextButton.toItemStack());
        }

        ItemBuilder backButton = new ItemBuilder(Material.ARROW).setName("Back");
        inventory.setItem(inventory.getSize() - 1, backButton.toItemStack());

        ConfigEditorMenu.placeFiller(inventory, editor);
        editor.openInventory(inventory);
        return this;
    }

    public KeyInfo matchInfo(String key) {
        return keyInfos.stream().filter(r -> r.path.equalsIgnoreCase(key)
                // check for cases with wildcards
                || Pattern.matches((r.path.endsWith("*") ? r.path.replace(".", "\\.").replace("*", "[^\\.]*") + "$" : r.path.replace(".", "\\.").replace("*", "[^\\.]*")), key)
        ).findFirst().orElse(null);
    }

    public String getDescription(String key) {
        KeyInfo info = matchInfo(key);
        if (info == null || info.description == null)
            return defaultDesc;

        return info.description;
    }

    public String getPathWithKey(String key) {
        if (currentSection.isEmpty())
            return key;
        else
            return currentSection + "." + key;
    }

    private KeyType getKeyType(String key) {
        if (matchInfo(key) != null) {
            KeyInfo info = matchInfo(key);
            if (info.type != null)
                return info.type;
        }
        try {
            return KeyType.getKeyType(config.get(key));
        } catch (Exception ev) {
            ev.printStackTrace();
        }

        return null;
    }

    private ItemStack getItemForKey(String key, KeyInfo info) {
        Material material = info != null ? info.displayMaterial : Material.matchMaterial(defaultMaterial);
        ItemBuilder stack = new ItemBuilder(material).setName(Text.modify("&a&l" + getLastKey(key)));

        String desc = getDescription(key);
        if (desc != null && !desc.isEmpty()) {
            stack.addLoreLine(Text.modify(" &f&l\u24D8 &f" + desc));
        }

        if (config.contains(key)) {
            if (config.get(key) instanceof List) {
                addListValues(stack, key);
            } else if (!(config.get(key) instanceof ConfigurationSection)) {
                addNonSectionValues(stack, key);
            } else {
                addSectionValues(stack, key);
                stack.addLoreLine("");
                stack.addLoreLine(Text.modify("&fOpen a sub-menu to edit."));
            }
        } else
            stack.addLoreLine(Text.modify("&eValue is not set in the configuration."));

        addTypeInfo(stack, key);
        stack.addLoreLine(" ").addLoreLine(Text.modify(" &7\u27A4 &lLeft Click&7 here to edit this value"));

        if (config.contains(key))
            stack.addLoreLine(Text.modify(" &c\u2715 &lShift Left Click&c here to delete this value"));

        if (info != null && info.wikiLink != null)
            stack.addLoreLine(Text.modify(" &f\u24D8 &lRight Click&7 here to learn more"));

        stack.addItemFlag(ItemFlag.values());
        return stack.toItemStack();
    }

    private void addListValues(ItemBuilder stack, String key) {
        List<String> values = (List<String>) config.get(key);
        stack.addLoreLine(Text.modify("&eCurrent values: "));
        for (String value : values) {
            stack.addLoreLine(Text.modify("&6&l \uD83D\uDD39 &f" + ASManager.limit(value, 40, "...")));
        }
    }

    private void addNonSectionValues(ItemBuilder stack, String key) {
        stack.addLoreLine(Text.modify("&eCurrent value&f: " + config.get(key).toString()));
        KeyType keyType = getKeyType(key);
        if (keyType != null) {
            if (keyType.equals(KeyType.MATERIAL)) {
                Material m = Material.matchMaterial(config.getString(key));
                if (m != null) stack.setType(m);
            } else if (keyType.equals(KeyType.INTEGER)) {
                int i = Math.max(1, Math.min(64, config.getInt(key)));
                stack.setAmount(i);
            }
        }
    }

    private void addSectionValues(ItemBuilder stack, String key) {
        for (String entry : config.getConfigurationSection(key).getKeys(false).stream().limit(8).collect(Collectors.toList())) {
            Object value = config.get(key + "." + entry);
            KeyType t = KeyType.getKeyType(value);
            if (t == null) continue;

            value = formatSectionValue(value, t);
            stack.addLoreLine(Text.modify("&e\u2666 &6&l" + entry + " &f" + value));
        }
    }

    private Object formatSectionValue(Object value, KeyType t) {
        if (t.equals(KeyType.LIST)) {
            value = "List";
        } else if (t.equals(KeyType.KEY)) {
            value = "sub-menu";
        } else if (t.equals(KeyType.STRING)) {
            value = ((String) value).length() > 32 ? ((String) value).substring(0, 31) + "..." : value;
        }
        return value;
    }

    private void addTypeInfo(ItemBuilder stack, String key) {
        KeyType keyType = getKeyType(key);
        if (keyType != null) {
            stack.addLoreLine(Text.modify("&aValue Type: &f" + keyType.getFriendlyName()));
        }
    }

    private List<KeyInfo> getNotSetKeys() {
        String finalPath = currentSection;
        if (finalPath == null)
            return Collections.emptyList();

        return keyInfos.stream()
                .filter(this::isSameBlock)
                .collect(Collectors.toList());
    }

    private boolean isSameBlock(KeyInfo info) {
        if (info == null)
            return false;

        if (currentSection.isEmpty()) {
            return !info.path.contains(".");
        }

        String remainingPath = info.path.replace("*", getLastKey(currentSection)).replaceFirst(currentSection + ".", "");
        return info.path.replace("*", getLastKey(currentSection)).startsWith(currentSection) && !remainingPath.contains(".");
    }

    public void close() {
        enabled = false;
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null)
            return;

        boolean editingList = false;
        if (event.getInventory().equals(listEditor)) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            String line = ColorUtils.stripColor(clickedItem.getItemMeta().getDisplayName());

            if ("Go back".equals(clickedItem.getItemMeta().getDisplayName())) {
                // If the user clicked on the "Go back" item, go back to the previous GUI
                open();
                return;
            }

            if (event.isRightClick()) {
                list.remove(line);

                config.set(editingKey, list);
                close();
                ConfigEditorMenu.getHandler().updateFiles(editObject, editor, editingKey, currentSection);
                return;
            } else {
                if ("Add new line".equals(line)) {
                    // If the user clicked on the "Add new line" item, prompt them to enter the new line in chat
                    editingLine = -1;  // Use -1 to indicate that a new line is being added
                } else {
                    // If the user clicked on a line item, prompt them to edit the line in chat
                    editingLine = list.indexOf(line);
                }
                editingList = true;
            }
        }

        if (event.getInventory().equals(inventory) || editingList) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;
            String key = ColorUtils.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (key.isEmpty() || key.equals(" "))
                return;

            if ("Back".equals(key)) {

                if (baseSection.length() < this.currentSection.length()) {
                    currentSection = getPreviousSection();
                    open();
                } else {
                    close();
                    ConfigEditorMenu.getHandler().openMainMenu(editor);
                }
                return;
            }

            if ("Next Page".equals(key)) {
                page++;
                open();
                return;
            } else if ("Previous Page".equals(key)) {
                page--;
                open();
                return;
            }

            // adjust key for full path
            key = getPathWithKey(key);

            if (key.endsWith("Add new entry.")) {
                key = currentSection + ".*";
            }

            KeyInfo info = matchInfo(key);

            if (event.getClick().isRightClick() && info != null && info.wikiLink != null) {
                TextComponent tmessage = new TextComponent(Text.modify("&7&l\u24D8 &aClick here to open the page explaining '&f" + key + "&a' setting."));
                tmessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, info.wikiLink));
                editor.spigot().sendMessage(tmessage);

                editor.closeInventory();
                return;
            }

            if (event.isLeftClick() && event.isShiftClick()) {
                config.set(key, null);

                close();
                ConfigEditorMenu.getHandler().updateFiles(editObject, editor, key, key);
                return;
            }

            if (config.get(key) instanceof ConfigurationSection || info != null && info.menu) {
                if (config.get(key) == null) {
                    config.createSection(key);
                }

//                currentSection = currentSection.isEmpty() ? key : currentSection + "." + key;
                currentSection = key;
                open();
//                new ConfigEditorGui(ConfigEditorMenu.getHandler().getTextColor() + "&l" + getLastKey(key) + ConfigEditorMenu.getHandler().getTextColor() +
//                        " editor", key, config.getConfigurationSection(key), editor, keyInfos, editObject, plugin).open();
            } else {
                editingKey = !editingList ? key : editingKey;
                Object value = config.get(editingKey);

                KeyType type = getKeyType(key);
                if (value instanceof List || (type != null && type.equals(KeyType.LIST))) {
                    // Open a new GUI for editing the list
                    if (!editingList) {
                        editingKey = key;
                        openListEditorGui(key);
                        return;
                    } else {
                        if (editingLine < 0)
                            value = null;
                        else
                            value = ((List) value).get(editingLine);
                    }
                } else {
                    // It's definitely not a list, so we can edit it.
                    editingKey = key;
                }

                // just flip a boolean, there's 2 options of it anyway
                if (type != null && type.equals(KeyType.BOOLEAN) && value != null) {
                    boolean currentValue = (Boolean) value;
                    processInput((!currentValue) + "");
                    return;
                }

                editor.closeInventory();
                editor.sendMessage(Text.modify("&8-------------------------------------------------------"));
                editor.sendMessage(Text.modify(" "));
                editor.sendMessage(Text.modify(" "));
                editor.sendMessage(Text.modify(" "));
                editor.sendMessage(Text.modify(" "));


                String message = "Please enter a value for &e'" + key + "'&o";
                if (type != null) {
                    message += " (" + type.getFriendlyName() + ")";
                }

                message += "&f or type &e'cancel'&f to cancel. \n&f&nCurrent value:&e ";
                editor.sendMessage(Text.modify(message) + (value == null ? "not set" : value.toString()));
                editor.sendMessage(Text.modify(" "));

                if (info != null && info.wikiLink != null) {
                    TextComponent tmessage = new TextComponent(Text.modify("&7&l\u24D8 &eClick here to open the page explaining '&f" + key + "&e' setting."));
                    tmessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, info.wikiLink));
                    editor.spigot().sendMessage(tmessage);
                }

                if (value != null) {
                    TextComponent tmessage = new TextComponent(Text.modify("&7&l\u24D8 &aClick here to insert &f" + value.toString() + "&a to your chat."));
                    tmessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, value.toString()));
                    editor.spigot().sendMessage(tmessage);
                }

                if (info != null && info.suggestions != null) {
                    editor.sendMessage(Text.modify("&7&oYou can use TAB to auto-complete suggestions for this value!"));
                    editor.addCustomChatCompletions(info.suggestions);
                }
            }
        }
    }

    private String getPreviousSection() {
        String[] path = currentSection.split("\\.");
        if (path.length == 1) {
            return "";
        } else if (path.length == 2) {
            return path[0];
        }

        return String.join(".", Arrays.copyOfRange(path, 0, path.length - 1));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().equals(editor) && editingKey != null) {
            SchedulerUtils.runTaskLater(() -> {
                if (validateInput(event.getMessage())) {
                    processInput(event.getMessage());
                    if (enabled) open();
                } else {
                    editor.sendMessage("Invalid input: " + event.getMessage());
                }

            });
            event.setCancelled(true);
            return;
        }
    }

    private void processInput(String input) {
        if (input.equalsIgnoreCase("cancel")) {
            open();
        } else {
//            editingKey = currentSection + "." + editingKey;
            KeyType type = getKeyType(editingKey);

            if (type == null) {
                editor.sendMessage(Text.modify("&c&l(!) &cInvalid value type: " + input));
            } else {
                // handle input parsing settings
                KeyInfo info = matchInfo(editingKey);
                if (info != null) {
                    if (info.addKeyToInput) {
                        input = editingKey + "." + input;
                    } else if (info.type.equals(KeyType.KEY)) {
                        input = currentSection + "." + input;
                    }
                }

                Object parsed = type.process(input, config);

                // checking if we not are editing a list
                if (editingLine == -2) {
                    if (!parsed.equals(KeyType.KEY))
                        config.set(editingKey, parsed);
                } else {
                    // we are editing a list
                    List list = config.getList(editingKey);
                    if (editingLine != -1)
                        list.set(editingLine, parsed);
                    else
                        list.add(parsed);

                    config.set(editingKey, list);
                    editingLine = -2;
                }

                close();
                ConfigEditorMenu.getHandler().updateFiles(editObject, editor, editingKey, currentSection);
            }
        }

        if (matchInfo(editingKey) != null && matchInfo(editingKey).suggestions != null)
            editor.removeCustomChatCompletions(matchInfo(editingKey).suggestions);

        editingKey = null;
    }

    public String getLastKey(String key) {
        String[] split = key.split("\\.");
        return split[split.length - 1];
    }

    private boolean validateInput(String input) {
        if (input.equalsIgnoreCase("cancel"))
            return true;

        KeyType type = getKeyType(editingKey);
        return type != null && type.validate(input);
    }

    public ConfigEditorGui setDefaultDesc(String desc) {
        this.defaultDesc = desc;
        return this;
    }

    public ConfigEditorGui setCurrentSection(String section) {
        this.currentSection = section;
        return this;
    }

    public ConfigEditorGui setDefaultMaterial(String mat) {
        this.defaultMaterial = mat;
        return this;
    }

    // The index of the line in the list that is currently being edited
    private int editingLine = -2;

    // The original list before any edits were made
    private List<String> originalList;
    private List<String> list;

    private Inventory listEditor = null;

    public void openListEditorGui(String key) {
        list = config.contains(key) ? config.getStringList(key) : new ArrayList<>();

        // Create a new inventory for editing the list
        listEditor = Bukkit.createInventory(null, ASManager.getInvSize(list.size() + 1), Text.modify("&eEdit list: ") + key);

        // Add an item for each line in the list
        for (String s : list) {
            ItemStack lineItem = new ItemStack(Material.PAPER);
            ItemMeta meta = lineItem.getItemMeta();
            meta.setDisplayName(s);
            meta.setLore(Arrays.asList(Text.modify(" &7\u27A4 &nLeft Click&7 here to edit this line.")
                    , Text.modify(" &7\u2715 &nRight Click&7 here to remove this line.")));
            lineItem.setItemMeta(meta);
            lineItem = NBTapi.addNBTTag("randomizer", UUID.randomUUID().toString(), lineItem);
            listEditor.addItem(lineItem);
        }

        // Add a special item for adding a new line to the list
        ItemStack newItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta newMeta = newItem.getItemMeta();
        newMeta.setDisplayName("Add new line");
        newItem.setItemMeta(newMeta);
        listEditor.setItem(list.size(), newItem);

        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName("Go back");
        backButton.setItemMeta(backMeta);
        listEditor.setItem(listEditor.getSize() - 1, backButton);

        // Open the list editor GUI
        editor.openInventory(listEditor);
    }

    public void addItemForPath(String path) {
//        LinkedList<KeyInfo> keyInfos = new LinkedList<>();
        if (matchInfo("item") == null) {
            keyInfos.add(new KeyInfo(path, Material.CRAFTING_TABLE, "Configure the item", null, "https://wiki.advancedplugins.net/configuration/config-items").setType(KeyType.KEY));
            keyInfos.add(new KeyInfo(path + "." + "type", Material.STICK, "Set item's material", null, "https://wiki.advancedplugins.net/configuration/config-items").setType(KeyType.MATERIAL).addSuggestions(Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList())));
            keyInfos.add(new KeyInfo(path + "." + "amount", Material.CHEST, "Set item's amount", null, "https://wiki.advancedplugins.net/configuration/config-items").setType(KeyType.INTEGER).addSuggestions(Arrays.asList("1", "10", "16", "64")));
            keyInfos.add(new KeyInfo(path + "." + "name", Material.PAPER, "Set name", null, "https://wiki.advancedplugins.net/configuration/config-items").setType(KeyType.STRING));
            keyInfos.add(new KeyInfo(path + "." + "lore", Material.BOOK, "Set item's lore", null, "https://wiki.advancedplugins.net/configuration/config-items").setType(KeyType.LIST));
            keyInfos.add(new KeyInfo(path + "." + "force-glow", Material.GLOWSTONE, "Toggle item's glowing status", Arrays.asList("TRUE", "FALSE"), "https://wiki.advancedplugins.net/configuration/config-items").setType(KeyType.BOOLEAN));
            keyInfos.add(new KeyInfo(path + "." + "custom-model-data", Material.COMMAND_BLOCK, "Change item's custom model data", null, "https://wiki.advancedplugins.net/configuration/config-items").setType(KeyType.INTEGER));
            keyInfos.add(new KeyInfo(path + "." + "enchantments", Material.ENCHANTING_TABLE, "Change item's enchantments", null, "https://wiki.advancedplugins.net/configuration/config-items").setType(KeyType.LIST));
            keyInfos.add(new KeyInfo(path + "." + "custom-enchantments", Material.ENCHANTING_TABLE, "Change item's custom enchantments", null, "https://wiki.advancedplugins.net/configuration/config-items").setType(KeyType.LIST));
        }
//
//        for (KeyInfo info : keyInfos) {
//            info.path = path + "." + info.path;
//        }
//        for (String k : new String[]{"item", "type", "amount", "name", "lore", "force-glow", "custom-model-data", "enchantments", "custom-enchantments"}) {
//
//        }
    }

    public LinkedList<KeyInfo> getKeyInfos() {
        return keyInfos;
    }

    public ConfigurationSection getConfig() {
        return this.config;
    }

//    public Map<String, KeyInfo> getKeyInfos() {
//        return keyInfos;
//    }
}
