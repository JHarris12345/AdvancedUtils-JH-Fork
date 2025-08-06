package net.advancedplugins.utils.hooks;

import lombok.Getter;

@Getter
public enum HookPlugin {
    ADVANCEDENCHANTMENTS("AdvancedEnchantments"),
    ADVANCEDSKILLS("AdvancedSkills"),
    WORLDGUARD("WorldGuard"),
    TOWNY("Towny"),
    TOWNYCHAT("TownyChat"),
    LWC("LWC"),
    LANDS("Lands"),
    GRIEFPREVENTION("GriefPrevention"),
    GRIEFDEFENDER("GriefDefender"),
    SLIMEFUN("Slimefun"),
    ADVANCEDPETS("AdvancedPets"),
    DYNMAP("dynmap"),
    RESIDENCE("Residence"),
    AURASKILLS("AuraSkills"),
    PLACEHOLDERAPI("PlaceholderAPI"),
    ESSENTIALS("Essentials"),
    CMI("CMI"),
    FACTIONS("Factions"),
    ITEMSADDER("ItemsAdder"),
    MYTHICMOBS("MythicMobs"),
    GEYSER("Geyser-Spigot"),
    MCMMO("mcMMO"),
    SUPERIORSKYBLOCK2("SuperiorSkyblock2"),
    ORAXEN("Oraxen"),
    PROTECTIONSTONES("ProtectionStones"),
    PROTOCOLLIB("ProtocolLib"),
    FACTIONSKORE("FactionsKore"),
    ADVANCEDCHESTS("AdvancedChests"),
    BEACONPLUS3("BeaconPlus3"),
    LUCKPERMS("LuckPerms"),
    VAULT("Vault"),
    VIAVERSION("ViaVersion"),
    PREMIUMVANISH("PremiumVanish"),
    SUPERVANISH("SuperVanish"),
    DISCORDSRV("DiscordSRV");

    private final String pluginName;

    HookPlugin(String pluginName) {
        this.pluginName = pluginName;
    }
}
