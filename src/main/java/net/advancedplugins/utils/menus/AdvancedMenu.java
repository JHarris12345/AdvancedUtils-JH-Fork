package net.advancedplugins.utils.menus;

import lombok.Getter;
import lombok.Setter;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.menus.item.AdvancedMenuItem;
import net.advancedplugins.utils.menus.item.ClickAction;
import net.advancedplugins.utils.text.Replace;
import net.advancedplugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class AdvancedMenu implements InventoryHolder {

    //    private final ConfigurationSection section;
    private final AdvancedMenusHandler handler = AdvancedMenusHandler.getInstance();
    private Inventory inventory;

    private final LinkedHashMap<Integer, AdvancedMenuItem> itemHashMap = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, ClickAction> actionMap = new LinkedHashMap<>();

    @Setter
    private AdvancedMenuItem fillerItem = null;

    @Getter
    private final Player player;

    @Getter
    @Setter
    private Replace replace;

    @Getter
    @Setter
    private int page = 0;

    @Getter
    private final String title;
    @Getter
    private final int invSize;

    private ClickAction closeAction = null;
    private final ConfigurationSection section;

    public AdvancedMenu(Player player, ConfigurationSection section, Replace replace) {
//        this.section = section;
        this.player = player;
        this.title = ASManager.limit(Text.modify(section.getString(handler.getPath("name")), (Replace) replace), 16, "");
        this.invSize = section.getInt(handler.getPath("size"));
        this.replace = replace;
        this.section = section;

        populateItemHashMap(section, itemHashMap, replace);
    }

    public void refreshItems() {
        itemHashMap.clear();
        populateItemHashMap(section, itemHashMap, replace);
    }

    public void openInventory() {
        openInventory(null);
    }

    public void openInventory(Integer page) {
        inventory = Bukkit.createInventory(this, this.invSize, title);
        if (page != null) {
            page = Math.max(0, page);
            this.page = page;
        }

        itemHashMap.values().forEach(i -> {
            try {
                i.addToInventory(inventory);
            } catch (Exception ev) {
                ASManager.log("[AdvancedMenu] Error adding item to inventory: ["+i.getSlots()+"] " + i.getItem());
                ev.printStackTrace();
            }
        });

        if (fillerItem != null) {
            ASManager.fillEmptyInventorySlots(inventory, fillerItem.getItem());
        }

        player.openInventory(inventory);
    }

    // handle clicks
    protected void onClick(Player player, int slot, ClickType type) {
        AdvancedMenuItem item = itemHashMap.get(slot);

        if (item == null)
            return;

        // If there's no action associated with the item, check actionMap
        if (item.getAction() == null) {
            ClickAction action = actionMap.get(slot);
            if (action == null) return;  // Early return if action is null
            action.onClick(player, this, item, slot, type);
            return;
        }

        // If the item has an action, get it from handler's default actions
        ClickAction defaultAction = handler.getDefaultActions().get(item.getAction());
        if (defaultAction == null) return;  // Early return if action is null
        defaultAction.onClick(player, this, item, slot, type);
    }

    // handle inventory close
    protected void onClose(Player player) {
        if (closeAction == null) // no action
            return;
        closeAction.onClick(player, this, null, 0, null);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void populateItemHashMap(ConfigurationSection section, HashMap<Integer, AdvancedMenuItem> itemMap, Replace replace) {
        String itemsPath = handler.getPath("items");
        if (!section.isConfigurationSection("items"))
            return;

        ConfigurationSection itemsConfigSection = section.getConfigurationSection(itemsPath);

        for (String itemKey : itemsConfigSection.getKeys(false)) {
            processItemKey(itemKey, itemsConfigSection, itemMap, replace);
        }
    }

    private void processItemKey(String itemKey, ConfigurationSection itemsConfigSection, HashMap<Integer, AdvancedMenuItem> itemMap, Replace replace) {
        processItemKey(itemKey, itemsConfigSection.getCurrentPath() + "." + itemKey, itemsConfigSection, itemMap, replace);
    }

    public void processItem(String itemKey, ConfigurationSection itemsConfigSection, Replace replace) {
        processItemKey(itemKey, itemsConfigSection.getCurrentPath() + "." + itemKey, itemsConfigSection, this.itemHashMap, replace);
    }

    public void processItem(String itemKey, ConfigurationSection itemsConfigSection) {
        processItemKey(itemKey, itemsConfigSection.getCurrentPath() + "." + itemKey, itemsConfigSection, this.itemHashMap, replace);
    }

    private void processItemKey(String itemKey, String itemPath, ConfigurationSection itemsConfigSection, HashMap<Integer, AdvancedMenuItem> itemMap, Replace replace) {
        ConfigurationSection itemConfigSection = itemsConfigSection.getConfigurationSection(itemKey);

        if (itemKey.equalsIgnoreCase("filler")) {
            fillerItem = new AdvancedMenuItem(itemKey, itemConfigSection, replace);
            return;
        }

        for (int slot : ASManager.getSlots(itemKey)) {
            assert itemConfigSection != null;
            itemMap.put(slot, new AdvancedMenuItem(itemKey, itemConfigSection, replace));
        }
    }

    public AdvancedMenu addItem(AdvancedMenuItem item, int... slots) {
        for (int i : slots) {
            itemHashMap.put(i, item);
        }
        return this;
    }

    public AdvancedMenu addAction(ClickAction action, int... slots) {
        for (int i : slots) {
            actionMap.put(i, action);
        }
        return this;
    }

    public AdvancedMenu addCloseAction(ClickAction action) {
        this.closeAction = action;
        return this;
    }
}
