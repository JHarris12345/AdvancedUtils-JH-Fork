package net.advancedplugins.utils.menus;

import lombok.Getter;
import net.advancedplugins.utils.menus.item.ClickAction;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class AdvancedMenusHandler {

    // inventory paths
    private final HashMap<String, String> paths = new HashMap<>();
    private static AdvancedMenusHandler instance = null;

    @Getter
    private final HashMap<String, ClickAction> defaultActions = new HashMap<>();

    public AdvancedMenusHandler(JavaPlugin plugin) {
        instance = this;

        paths.put("name", "name");
        paths.put("size", "size");
        paths.put("items", "items");

        loadDefaultActions();

        // Register inventory click handler
        plugin.getServer().getPluginManager().registerEvents(new AdvancedMenuClick(), plugin);
    }

    private void loadDefaultActions() {
        defaultActions.put("close", (player, inventory, item, slot, type) -> player.closeInventory());
        // Todo: add previous_page, next_page action
    }

    public String getPath(String type) {
        return paths.getOrDefault(type, type);
    }

    public static AdvancedMenusHandler getInstance() {
        return instance;
    }
}
