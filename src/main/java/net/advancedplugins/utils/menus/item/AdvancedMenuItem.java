package net.advancedplugins.utils.menus.item;

import lombok.Getter;
import lombok.Setter;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.items.ConfigItemCreator;
import net.advancedplugins.utils.items.ItemBuilder;
import net.advancedplugins.utils.text.Replace;
import net.advancedplugins.utils.text.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class AdvancedMenuItem {

    private ConfigurationSection section;
    private Replace replace;
    private int[] slots;

    @Getter
    private String action = null;

    private ItemStack item;

    private boolean glow = false;
    private int amount = 0;

    /**
     * Used for custom data passing, e.g. map keys etc.
     */
    @Getter
    private String data;

    public AdvancedMenuItem(String slots, ConfigurationSection section, Replace replace) {
        this.slots = ASManager.getSlots(slots);
        this.replace = replace;
        this.section = section;

        if (section.contains("action")) {
            action = section.getString("action");
        }
    }

    public AdvancedMenuItem(int slot, ConfigurationSection section, Replace replace) {
        this.slots = new int[]{slot};
        this.replace = replace;
        this.section = section;

        if (section.contains("action")) {
            action = section.getString("action");
        }
    }

    public AdvancedMenuItem(int[] slots, ConfigurationSection section, Replace replace) {
        this.slots = slots;
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

    public AdvancedMenuItem setGlow() {
        glow = true;
        return this;
    }

    public AdvancedMenuItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack getItem() {
        if (item != null)
            return item;

        ItemStack item = ConfigItemCreator.fromConfigSection(section, "",
                replace == null ? null : replace.apply(new Replacer()).getPlaceholders(), null);
        if (glow) {
            ItemBuilder builder = new ItemBuilder(item);
            builder.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
            builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            item = builder.toItemStack();
        }

        if (amount != 0) {
            item.setAmount(amount);
        }
        return item;
    }

    public AdvancedMenuItem setData(String data) {
        this.data = data;
        return this;
    }

    public String getSlots() {
        return Arrays.toString(slots);
    }

    public AdvancedMenuItem setSlots(int... slots) {
        this.slots = slots;
        return this;
    }
}
