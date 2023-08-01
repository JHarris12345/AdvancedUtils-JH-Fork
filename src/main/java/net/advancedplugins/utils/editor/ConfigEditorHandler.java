package net.advancedplugins.utils.editor;

import org.bukkit.entity.Player;

public interface ConfigEditorHandler {

    boolean canCreateNewEntries();

    String getGlassColor();

    String getTextColor();

    ConfigEditorGui create(String name, Player player);

    ConfigEditorGui updateFiles(KeyInfo info, Player player, String key, String path);

    void openMainMenu(Player p);

    public ConfigEditorGui openEditor(KeyInfo key, Player editor);

    public ConfigEditorGui openEditor(KeyInfo key, Player editor, String pathToOpen);

}
