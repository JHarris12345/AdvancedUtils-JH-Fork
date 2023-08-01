package net.advancedplugins.utils.editor;

import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.SchedulerUtils;
import net.advancedplugins.utils.items.ItemBuilder;
import net.advancedplugins.utils.nbt.NBTapi;
import net.advancedplugins.utils.text.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.stream.Collectors;

public class ConfigEditorGui implements Listener {

    public enum EditorType {
        DEFAULT,
        ENCHANT_MENU;
    }

    private Inventory inventory;
    private ConfigurationSection config;
    private Player editor;
    private String editingKey;
    private LinkedHashMap<String, KeyInfo> keyInfos = new LinkedHashMap<>();
    private ConfigEditorGui parent;
    private List<String> savedTabCompletions = null;
    private JavaPlugin plugin;

    private String defaultDesc = "";
    private String defaultMaterial = "PAPER";

    private final KeyInfo editObject;

    private int page = 0;
    private static final int ITEMS_PER_PAGE = 27;

    // Built-in mechanics
    private EditorType type = EditorType.DEFAULT;

    private boolean enabled = true;

    public ConfigEditorGui(String name, ConfigurationSection config, Player player, Map<String, KeyInfo> keyInfos, ConfigEditorGui parent, KeyInfo editObject, JavaPlugin plugin) {
        this.config = config;
        this.keyInfos.putAll(keyInfos);
        this.editor = player;
        this.parent = parent;
        this.plugin = plugin;
        this.editObject = editObject;
        this.inventory = Bukkit.createInventory(null, 18, Text.modify(name));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public ConfigEditorGui open() {
        inventory.clear();

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, config.getKeys(false).size());
        List<String> keys = new ArrayList<>(config.getKeys(false));

        for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
            inventory.setItem(i, new ItemBuilder(Material.matchMaterial(ConfigEditorMenu.getHandler().getGlassColor() + "_STAINED_GLASS_PANE"))
                    .setName(" ").setGlowing(true).toItemStack());
        }

        for (int i = start; i < end; i++) {
            String key = keys.get(i);
            KeyInfo info = keyInfos.get(getKey(key));

            inventory.addItem(getItemForKey(key, info));
        }

        for (String unsetKey : getNotSetKeys()) {
            KeyInfo info = keyInfos.get(unsetKey);
            if (ASManager.getKeysByValue(keyInfos, info).stream().findAny().get().endsWith("*"))
                continue;
            inventory.addItem(getItemForKey(unsetKey, info));
        }

        if (keyInfos.containsKey(config.getCurrentPath() + ".*")) {
//        if (!getKeyPath(config.getCurrentPath() + ".X").isEmpty()) {
            inventory.addItem(new ItemBuilder(Material.GREEN_WOOL)
                    .setName(Text.modify("&a&lAdd new entry."))
                    .toItemStack());
        }

        if (page > 0) {
            ItemStack prevButton = new ItemStack(Material.BOOK);
            prevButton.setAmount(page);
            ItemMeta meta = prevButton.getItemMeta();
            meta.setDisplayName("Previous Page");
            prevButton.setItemMeta(meta);
            inventory.setItem(inventory.getSize() - 6, prevButton);
        }
        if (end < config.getKeys(false).size()) {
            ItemStack nextButton = new ItemStack(Material.BOOK);
            nextButton.setAmount(page + 2);
            ItemMeta meta = nextButton.getItemMeta();
            meta.setDisplayName("Next Page");
            nextButton.setItemMeta(meta);
            inventory.setItem(inventory.getSize() - 4, nextButton);
        }

        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta meta = backButton.getItemMeta();
        meta.setDisplayName("Back");
        backButton.setItemMeta(meta);
        inventory.setItem(inventory.getSize() - 1, backButton);


        ConfigEditorMenu.placeFiller(inventory, editor);
        editor.openInventory(inventory);
        return this;
    }

    private String getKey(String key) {
        String path = config.getCurrentPath() + ".*";

        if (!keyInfos.containsKey(key)) {
            if (!keyInfos.containsKey(path))
                return key;
            else
                return path;
        }
        return key;
    }

    private Set<String> getNotSetKeys() {
        String path = getKeyPath(config.getCurrentPath() + (editingKey != null ? "." + editingKey : ""));
        if (path == null || path.isEmpty())
            path = parent == null && config.getCurrentPath() != null ? "" : config.getCurrentPath();

        String finalPath = path;
        if (finalPath == null)
            return Collections.emptySet();

        return keyInfos.entrySet().stream()
                .filter(k -> !config.contains(k.getKey()) &&
                        ((config.getParent() == null && k.getValue().parent.isEmpty()) || k.getValue().parent.contains(finalPath)))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private ItemStack getItemForKey(String key, KeyInfo info) {
        ItemStack stack = new ItemStack(info != null ? info.displayMaterial : Material.matchMaterial(defaultMaterial));
        String desc = null;

        if (hasDesc(key))
            desc = getDesc(key);
        else if (info != null && info.description != null) {
            desc = info.description;
        }

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Text.modify("&a&l" + key));

        List<String> lore = new ArrayList<>();
        if (desc != null) {
            lore.add(Text.modify(" &f&l\u24D8 &f" + desc));
//            lore.add(""); space looks weird
        }

        if (config.contains(key)) {
            if (config.get(key) instanceof List) {
                List<String> values = (List<String>) config.get(key);
                lore.add(Text.modify("&eCurrent values: "));
                for (String value : values) {
                    lore.add(Text.modify("&6&l \uD83D\uDD39 &f" + ASManager.limit(value, 40, "...")));
                }
            } else if (!(config.get(key) instanceof ConfigurationSection)) {
                lore.add(Text.modify("&eCurrent value&f: " + config.get(key).toString()));

                if (getKeyType(key) != null) {
                    if (getKeyType(key).equals(KeyType.MATERIAL)) {
                        Material m = Material.matchMaterial(config.getString(key));
                        if (m != null)
                            stack.setType(m);
                    } else if (getKeyType(key).equals(KeyType.INTEGER)) {
                        int i = Math.min(64, config.getInt(key));
                        stack.setAmount(i);
                    }
                }
            } else {
                for (String entry : config.getConfigurationSection(key).getKeys(false).stream()
                        .limit(8).collect(Collectors.toList())) {

                    Object value = config.get(key + "." + entry);
                    KeyType t = KeyType.getKeyType(value);
                    if (t == null)
                        continue;

                    if (t.equals(KeyType.LIST)) {
                        value = "List";
                    } else if (t.equals(KeyType.KEY)) {
                        value = "sub-menu";
                    } else if (t.equals(KeyType.STRING)) {
                        value = ((String) value).length() > 32 ? ((String) value).substring(0, 31) + "..." : value;
                    }

                    lore.add(Text.modify("&e\u2666 &6&l" + entry + " &f" + value));
                }
                lore.add("");
                lore.add(Text.modify("&fOpen a sub-menu to edit."));
            }
        } else {
            lore.add(Text.modify("&eValue is not set in the configuration."));
        }

        if (getKeyType(key) != null) {
            lore.add(Text.modify("&aValue Type: &f" + getKeyType(key).getFriendlyName()));
        }

        lore.add(" ");
        lore.add(Text.modify(" &7\u27A4 &lLeft Click&7 here to edit this value"));
        if (config.contains(key)) {
            lore.add(Text.modify(" &c\u2715 &lShift Left Click&c here to delete this value"));
        }

        if (info != null && info.wikiLink != null) {
            lore.add(Text.modify(" &f\u24D8 &lRight Click&7 here to learn more"));
        }

        meta.addItemFlags(ItemFlag.values());
        meta.setLore(lore);

        stack.setItemMeta(meta);
        return stack;
    }

    public void close() {
        enabled = false;
        HandlerList.unregisterAll(this);
        if (savedTabCompletions != null)
            editor.setCustomChatCompletions(savedTabCompletions);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null)
            return;

        boolean editingList = false;
        if (event.getInventory().equals(listEditor)) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            String line = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if ("Go back".equals(clickedItem.getItemMeta().getDisplayName())) {
                // If the user clicked on the "Go back" item, go back to the previous GUI
                editor.closeInventory();
                open();
                return;
            }

            if (event.isRightClick()) {
                list.remove(line);

                config.set(editingKey, list);
                close();
                editor.closeInventory();
                ConfigEditorMenu.getHandler().updateFiles(editObject, editor, editingKey, getCurrentPath());
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
            String key = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            if (key.equalsIgnoreCase("Add new entry.")) {
                key = "*";
            }

            if (key.isEmpty() || key.equals(" "))
                return;

            if ("Back".equals(key)) {
                close();
                if (parent != null)
                    parent.open();
                else {
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

            if (event.getClick().isRightClick() && keyInfos.get(key) != null && keyInfos.get(key).wikiLink != null) {
                TextComponent tmessage = new TextComponent(Text.modify("&7&l\u24D8 &aClick here to open the page explaining '&f" + key + "&a' setting."));
                tmessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, keyInfos.get(key).wikiLink));
                editor.spigot().sendMessage(tmessage);

                editor.closeInventory();
                return;
            }

            if (event.isLeftClick() && event.isShiftClick()) {
                config.set(key, null);

                close();
                editor.closeInventory();
                ConfigEditorMenu.getHandler().updateFiles(editObject, editor, editingKey, getCurrentPath());
                return;
            }

            if (config.get(key) instanceof ConfigurationSection || keyInfos.get(key) != null && keyInfos.get(key).menu) {
                if (config.get(key) == null) {
                    config.createSection(key);
                }

                new ConfigEditorGui(ConfigEditorMenu.getHandler().getTextColor() + "&l" + key + ConfigEditorMenu.getHandler().getTextColor() +
                        " editor", config.getConfigurationSection(key), editor, keyInfos, this, editObject, plugin).open();
            } else {
                editor.closeInventory();
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
                        value = ((List) value).get(editingLine);
                    }
                } else {
                    // It's definitely not a list, so we can edit it.
                    editingKey = key;
                }

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

                if (keyInfos.get(editingKey) != null && keyInfos.get(editingKey).wikiLink != null) {
                    TextComponent tmessage = new TextComponent(Text.modify("&7&l\u24D8 &eClick here to open the page explaining '&f" + key + "&e' setting."));
                    tmessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, keyInfos.get(key).wikiLink));
                    editor.spigot().sendMessage(tmessage);
                }

                if (value != null) {
                    TextComponent tmessage = new TextComponent(Text.modify("&7&l\u24D8 &aClick here to insert &f" + value.toString() + "&a to your chat bar."));
                    tmessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, value.toString()));
                    editor.spigot().sendMessage(tmessage);
                }

                if (keyInfos.get(key) != null && keyInfos.get(key).suggestions != null) {
                    editor.sendMessage(Text.modify("&7&oYou can use TAB to auto-complete suggestions for this value!"));
                    editor.addCustomChatCompletions(keyInfos.get(key).suggestions);
                }
            }
        }
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

    private String getKeyPath(String path) {
        // Iterate over all entries in keyInfos
        for (Map.Entry<String, KeyInfo> entry : keyInfos.entrySet()) {
            String key = entry.getKey();
            KeyInfo info = entry.getValue();

            if (info == null || info.parent.isEmpty())
                continue;

            // Replace '*' with '.*' to create a regex pattern
            for (String infoParent : info.parent) {
                String parentPattern = infoParent.replace("*", ".*");

                // Check if the path matches the pattern
                if (path.matches(parentPattern)) {
                    return infoParent;
                }
            }
        }

        return "";
    }

    private void processInput(String input) {
        if (input.equalsIgnoreCase("cancel")) {
            open();
        } else {
            KeyType type = getKeyType(editingKey);
            if (type == null) {
                editor.sendMessage(Text.modify("&c&l(!) &cInvalid value type: " + input));
            } else {
                // handle input parsing settings
                KeyInfo info = keyInfos.get(editingKey);
                if (info != null) {
                    if (info.addKeyToInput) {
                        input = editingKey + "." + input;
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
                editor.closeInventory();
                ConfigEditorMenu.getHandler().updateFiles(editObject, editor, editingKey, getCurrentPath());
            }
        }

        if (keyInfos.get(editingKey) != null && keyInfos.get(editingKey).suggestions != null)
            editor.removeCustomChatCompletions(keyInfos.get(editingKey).suggestions);

        editingKey = null;
    }

    private KeyType getKeyType(String key) {
        KeyType type = null;
        if (keyInfos.containsKey(key)) {
            KeyInfo info = keyInfos.get(key);
            if (info.type != null) {
                type = info.type;
            }
        } else {
            try {
                //(config.getCurrentPath().isEmpty() ? "" : config.getCurrentPath() + ".") +
                type = KeyType.getKeyType(config.get(key));
            } catch (Exception ev) {
                ev.printStackTrace();
            }
        }
        return type;
    }

    public String getCurrentPath() {
        return config.getCurrentPath().isEmpty() ? "" : config.getCurrentPath() + "." + editingKey;
    }

    private boolean validateInput(String input) {
        if (input.equalsIgnoreCase("cancel"))
            return true;

        KeyType type = getKeyType(editingKey);

        return type != null && type.validate(input);
    }

    public ConfigEditorGui setType(EditorType type) {
        this.type = type;
        return this;
    }


    public ConfigEditorGui setDefaultDesc(String desc) {
        this.defaultDesc = desc;
        return this;
    }

    public ConfigEditorGui setDefaultMaterial(String mat) {
        this.defaultMaterial = mat;
        return this;
    }

    public String getDesc(String key) {
        return keyInfos.containsKey(key) && keyInfos.get(key).description != null ? keyInfos.get(key).description : defaultDesc;
    }

    public boolean hasDesc(String key) {
        return (keyInfos.containsKey(key) && keyInfos.get(key).description != null) || !defaultDesc.isEmpty();
    }

    // The key of the list that is currently being edited
    private String editingListKey;

    // The index of the line in the list that is currently being edited
    private int editingLine = -2;

    // The original list before any edits were made
    private List<String> originalList;
    private List<String> list;

    private Inventory listEditor = null;

    public void openListEditorGui(String key) {
        list = config.contains(key) ? config.getStringList(key) : new ArrayList<>();

        // Create a new inventory for editing the list
        listEditor = Bukkit.createInventory(null, ASManager.getInvSize(list.size() + 1), "&eEdit list: " + key);

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
        if (!keyInfos.containsKey("item")) {
            keyInfos.put("item", new KeyInfo(Material.CRAFTING_TABLE, "Configure the item", null, "https://wiki.advancedplugins.net/configuration/config-items")
                    .setType(KeyType.KEY));
            keyInfos.put("type", new KeyInfo(Material.STICK, "Set item's material", null, "https://wiki.advancedplugins.net/configuration/config-items")
                    .setType(KeyType.MATERIAL)
                    .addSuggestions(Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList())));
            keyInfos.put("amount", new KeyInfo(Material.CHEST, "Set item's amount", null, "https://wiki.advancedplugins.net/configuration/config-items")
                    .setType(KeyType.INTEGER).addSuggestions(Arrays.asList("1", "10", "16", "64")));
            keyInfos.put("name", new KeyInfo(Material.PAPER, "Set name", null, "https://wiki.advancedplugins.net/configuration/config-items")
                    .setType(KeyType.STRING));
            keyInfos.put("lore", new KeyInfo(Material.BOOK, "Set item's lore", null, "https://wiki.advancedplugins.net/configuration/config-items")
                    .setType(KeyType.LIST));
            keyInfos.put("force-glow", new KeyInfo(Material.GLOWSTONE, "Toggle item's glowing status", Arrays.asList("TRUE", "FALSE"), "https://wiki.advancedplugins.net/configuration/config-items")
                    .setType(KeyType.BOOLEAN));
            keyInfos.put("custom-model-data", new KeyInfo(Material.COMMAND_BLOCK, "Change item's custom model data", null, "https://wiki.advancedplugins.net/configuration/config-items")
                    .setType(KeyType.INTEGER));
            keyInfos.put("enchantments", new KeyInfo(Material.ENCHANTING_TABLE, "Change item's enchantments", null, "https://wiki.advancedplugins.net/configuration/config-items")
                    .setType(KeyType.LIST));
            keyInfos.put("custom-enchantments", new KeyInfo(Material.ENCHANTING_TABLE, "Change item's custom enchantments", null, "https://wiki.advancedplugins.net/configuration/config-items")
                    .setType(KeyType.LIST));
        }

        for (String k : new String[]{"item", "type", "amount", "name", "lore", "force-glow", "custom-model-data", "enchantments", "custom-enchantments"}) {
            keyInfos.get(k).addParentPath(path);
        }
    }

    public Map<String, KeyInfo> getKeyInfos() {
        return keyInfos;
    }
}
