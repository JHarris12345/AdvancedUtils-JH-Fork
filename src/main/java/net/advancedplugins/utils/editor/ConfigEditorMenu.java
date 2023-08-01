package net.advancedplugins.utils.editor;

import lombok.Getter;
import lombok.Setter;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.SkullCreator;
import net.advancedplugins.utils.items.ItemBuilder;
import net.advancedplugins.utils.nbt.NBTapi;
import net.advancedplugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ConfigEditorMenu implements Listener {

    @Getter
    @Setter
    private static ConfigEditorHandler handler;

    private Inventory inv;
    private int page = 0;
    private final int totalPages;

    private final int entriesPerPage = 45;

    private final Player editor;
    private final LinkedList<KeyInfo> keyInfos;

    private final String inventoryName;

    private boolean creatingNew = false;

    public ConfigEditorMenu(Player player, String inventoryName, LinkedList<KeyInfo> keyInfos, JavaPlugin plugin) {
        this.editor = player;
        this.keyInfos = keyInfos;
        this.inventoryName = inventoryName;

        this.totalPages = ASManager.getPages(keyInfos.size(), entriesPerPage);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        inv = Bukkit.createInventory(null, 54, Text.modify(inventoryName));
        inv.setMaxStackSize(ThreadLocalRandom.current().nextInt(64, 256));
        List<KeyInfo> pageKeys = keyInfos.subList(page * entriesPerPage, Math.min((page + 1) * entriesPerPage, keyInfos.size()));

        int now = 1;
        for (KeyInfo keyInfo : pageKeys) {
            ItemBuilder builder = new ItemBuilder(keyInfo.displayMaterial);
            builder.setName(keyInfo.name);

            builder.addLoreLine(Text.modify(" &7&l(!) &7" + keyInfo.description));
            builder.addLoreLine(" ");
            builder.addLoreLine(Text.modify(" &7\u27A4 &nLeft Click&7 here to start editing."));
            if (keyInfo.wikiLink != null) {
                builder.addLoreLine(Text.modify(" &7\u24D8 &nRight Click&7 here to read more."));
            }

            builder.addItemFlag(ItemFlag.values());

            ItemStack item = builder.toItemStack();
            item = NBTapi.addNBTTag("editKey", keyInfo.name, item);
            item.setAmount(now);
            inv.addItem(item);

            now++;
        }

        for (int i = inv.getSize() - 9; i < inv.getSize(); i++) {
            inv.setItem(i, new ItemBuilder(Material.matchMaterial(handler.getGlassColor()+"_STAINED_GLASS_PANE"))
                    .setGlowing(true).setName(" ").toItemStack());
        }

        if (getHandler().canCreateNewEntries()) {
            inv.setItem(inv.getSize() - 1, NBTapi.addNBTTag("action", "create",
                    new ItemBuilder(Material.LIME_WOOL)
                            .setName(Text.modify("&a&lCreate a new one!"))
                            .addLoreLine(Text.modify("&e&lClick here to start the creation process."))
                            .toItemStack()));
        }

        if ((page + 1) < totalPages) {
            inv.setItem(inv.getSize() - 4, NBTapi.addNBTTag("action", "next",
                    new ItemBuilder(SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQub" +
                            "mV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19"))
                            .setName(Text.modify("&8>> &6Next Page"))
                            .setAmount((page + 1))
                            .toItemStack()));
        }

        if (page > 0) {
            inv.setItem(inv.getSize() - 6, NBTapi.addNBTTag("action", "back",
                    new ItemBuilder(SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQub" +
                            "mV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ"))
                            .setName(Text.modify("&8<< &6Previous Page"))
                            .setAmount(Math.max(1, page))
                            .toItemStack()));
        }

        placeFiller(inv, editor);
        editor.openInventory(inv);
    }

    static void placeFiller(Inventory inv, Player editor) {
        final ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").toItemStack();
        for (int i = 0; i < inv.getSize() - 9; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, filler);
            }
        }
    }

    private KeyInfo matchKeyInfoWithName(String name) {
        return keyInfos.stream().filter(r -> name.equals(r.name)).findFirst().orElse(null);
    }

    @EventHandler
    public void onCreate(AsyncPlayerChatEvent e) {
        if (creatingNew && e.getPlayer().equals(editor)) {
            handler.create(e.getMessage(), editor);
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(inv)) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();

            if (NBTapi.contains("action", clickedItem)) {
                String action = NBTapi.get("action", clickedItem);
                if (action.equalsIgnoreCase("back")) {
                    page--;
                    open();
                } else if (action.equalsIgnoreCase("next")) {
                    page++;
                    open();
                } else if (action.equalsIgnoreCase("create")) {
                    event.getWhoClicked().closeInventory();
                    editor.sendMessage(Text.modify("name for new item"));
                    creatingNew = true;
                }
                return;
            } else if (NBTapi.contains("editKey", clickedItem)) {
                ConfigEditorGui editorGui = handler.openEditor(matchKeyInfoWithName(NBTapi.get("editKey", clickedItem)), (Player) event.getWhoClicked());
                editorGui.open(); // todo: enable
                HandlerList.unregisterAll(this);
            }

        }
    }
}
