package net.advancedplugins.utils.hooks;

import lombok.Getter;

@Getter
public enum HookPlugin {
    ADVANCEDENCHANTMENTS("AdvancedEnchantments"),
    WORLDGUARD("WorldGuard"),
    TOWNY("Towny"),
    LWC("LWC"),
    LANDS("Lands"),
    GRIEFPREVENTION("GriefPrevention"),
    SLIMEFUN("Slimefun"),
    ADVANCEDPETS("AdvancedPets"),
    AURELIUMSKILLS("AureliumSkills"),
    PLACEHOLDERAPI("PlaceholderAPI"),
    FACTIONS("Factions"),
    ITEMSADDER("ItemsAdder"),
    MYTHICMOBS("MythicMobs"),
    MCMMO("mcMMO"),
    SUPERIORSKYBLOCK2("SuperiorSkyblock2"),
    ORAXEN("Oraxen"),
    PROTECTIONSTONES("ProtectionStones"),
    FACTIONSKORE("FactionsKore");

    private final String pluginName;
    HookPlugin(String pluginName) {
        this.pluginName = pluginName;
    }
}
