package net.advancedplugins.utils.hooks;

public enum HookPlugin {
    WORLDGUARD("WorldGuard"),
    GRIEFPREVENTION("GriefPrevention"),
    SLIMEFUN("SlimeFun"),
    ADVANCEDPETS("AdvancedPets"),
    FACTIONS("Factions"),
    MCMMO("mcMMO");

    private final String pluginName;
    HookPlugin(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return pluginName;
    }
}
