package net.advancedplugins.utils.menus.item;

import lombok.Getter;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.items.ConfigItemCreator;
import net.advancedplugins.utils.text.Replacer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdvancedMenuItem {

    private ConfigurationSection section;
    private Replacer replace;
    private int[] slots;

    @Getter
    private String action = null;

    private ItemStack item;

    public AdvancedMenuItem(String slots, ConfigurationSection section, Replacer replace) {
        this.slots = ASManager.getSlots(slots);
        this.replace = replace;
        this.section = section;

        if (section.contains("action")) {
            action = section.getString("action");
        }
    }

    public AdvancedMenuItem(ItemStack item) {
        this.item = item;
    }

    public void addToInventory(Inventory inv) {
        ItemStack item = getItem();
        for (int slot : slots) {
            inv.setItem(slot, item);
        }
    }

    public ItemStack getItem() {
        if (item != null)
            return item;

        return ConfigItemCreator.fromConfigSection(section, "", replace == null ? null : replace.getPlaceholders(), null);
    }
}
