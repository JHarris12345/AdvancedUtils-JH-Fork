package net.advancedplugins.utils.editor;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class KeyInfo {
    public Material displayMaterial;
    public String description;
    public List<String> suggestions;
    public String wikiLink = null;
    public HashSet<String> parent = new HashSet<>();
    public boolean menu = false;
    public KeyType type;

    public String name = null;

    public FileConfiguration configuration;

    public boolean addKeyToInput = false;

    public String path;

    public KeyInfo(String path, Material displayMaterial, String description, List<String> suggestions) {
        this.path = path;
        this.displayMaterial = displayMaterial;
        this.description = description;
        this.suggestions = suggestions;
    }

    public KeyInfo(String path, Material displayMaterial, String description, List<String> suggestions, String wikiLink) {
        this.path = path;
        this.displayMaterial = displayMaterial;
        this.description = description;
        this.suggestions = suggestions;
        this.wikiLink = wikiLink;
    }

    public KeyInfo addParentPath(String parent) {
        this.parent.add(parent);
        return this;
    }

    public KeyInfo setType(KeyType type) {
        this.type = type;
        return this;
    }

    public KeyInfo setName(String name) {
        this.name = name;
        return this;
    }

    public KeyInfo addSuggestions(List<String> suggestions) {
        if (this.suggestions == null)
            this.suggestions = new ArrayList<>();

        this.suggestions.addAll(suggestions);
        return this;
    }

    public KeyInfo asDefault() {
        this.parent.clear();
        return this;
    }

    public KeyInfo asMenu() {
        this.menu = true;
        return this;
    }

    public KeyInfo addKeyToInput() {
        this.addKeyToInput = true;
        return this;
    }
}