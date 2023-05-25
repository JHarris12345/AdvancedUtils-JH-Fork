package net.advancedplugins.utils.hooks;

public enum HookPlugin {
    WORLDGUARD("WorldGuard"),
    GRIEFPREVENTION("GriefPrevention"),
    SLIMEFUN("Slimefun"),
    ADVANCEDPETS("AdvancedPets"),
    AURELIUMSKILLS("AureliumSkills"),
    PLACEHOLDERAPI("PlaceholderAPI"),
    FACTIONS("Factions"),
    ITEMSADDER("ItemsAdder"),
    MYTHICMOBS("MythicMobs"),
    MCMMO("mcMMO");

    private final String pluginName;
    HookPlugin(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return pluginName;
    }
}
