package net.advancedplugins.utils.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class AdvancedMenuClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        ClickType action = e.getClick();

        if (!(inventory.getHolder() instanceof AdvancedMenu))
            return;

        AdvancedMenu menu = (AdvancedMenu) inventory.getHolder();
        e.setCancelled(true);

        int rawSlot = e.getRawSlot();
        if (action.equals(ClickType.UNKNOWN) || rawSlot > inventory.getSize())
            return;

        menu.onClick(((Player) e.getWhoClicked()), rawSlot, action);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        if (!(inventory.getHolder() instanceof AdvancedMenu))
            return;

        AdvancedMenu menu = (AdvancedMenu) inventory.getHolder();
        menu.onClose((Player) e.getPlayer());
    }
}
