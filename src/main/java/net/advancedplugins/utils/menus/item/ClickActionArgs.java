package net.advancedplugins.utils.menus.item;

import net.advancedplugins.utils.menus.AdvancedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface ClickActionArgs extends ClickAction {
    void onClick(Player player, AdvancedMenu menu, AdvancedMenuItem item, int slot, ClickType type, String args);
}