package net.advancedplugins.utils.hooks;

public enum HookPlugin {
    WORLDGUARD("WorldGuard"),
    GRIEFPREVENTION("GriefPrevention"),
    SLIMEFUN("SlimeFun"),
    MCMMO("mcMMO");

    private final String pluginName;
    HookPlugin(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return pluginName;
    }
}
