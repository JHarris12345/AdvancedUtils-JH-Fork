package net.advancedplugins.utils.menus;

import lombok.Getter;
import net.advancedplugins.utils.menus.item.ClickAction;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class AdvancedMenusHandler {

    // inventory paths
    private final HashMap<String, String> paths = new HashMap<>();
    @Getter
    private static AdvancedMenusHandler instance = null;

    @Getter
    private final HashMap<String, ClickAction> defaultActions = new HashMap<>();

    private final AdvancedMenuClick clickHandler = new AdvancedMenuClick();

    public AdvancedMenusHandler(JavaPlugin plugin) {
        instance = this;

        paths.put("name", "name");
        paths.put("size", "size");
        paths.put("items", "items");

        loadDefaultActions();

        // Register inventory click handler
        plugin.getServer().getPluginManager().registerEvents(clickHandler, plugin);
    }

    private void loadDefaultActions() {
        defaultActions.put("CLOSE", (player, inventory, item, slot, type) -> player.closeInventory());
        defaultActions.put("PREVIOUS_PAGE", (player, inventory, item, slot, type) -> {
            inventory.openInventory(inventory.getPage()-1);
        });
        defaultActions.put("NEXT_PAGE", (player, inventory, item, slot, type) -> {
            inventory.openInventory(inventory.getPage()+1);
        });
    }

    public String getPath(String type) {
        return paths.getOrDefault(type, type);
    }

    public void unload() {
        HandlerList.unregisterAll(clickHandler);
    }
}
