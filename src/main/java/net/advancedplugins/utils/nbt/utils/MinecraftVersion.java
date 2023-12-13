package net.advancedplugins.utils.nbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public enum MinecraftVersion {
    Unknown(Integer.MAX_VALUE),//Use the newest known mappings
    MC1_7_R4(1_7_4),
    MC1_8_R3(1_8_3),
    MC1_9_R1(1_9_1),
    MC1_9_R2(1_9_2),
    MC1_10_R1(1_10_1),
    MC1_11_R1(1_11_1),
    MC1_12_R1(1_12_1),
    MC1_13_R1(1_13_1),
    MC1_13_R2(1_13_2),
    MC1_14_R1(1_14_1),
    MC1_15_R1(1_15_0),
    MC1_16_R1(1_16_0),
    MC1_16_R2(1_16_1),
    MC1_16_R3(1_16_3),
    MC1_17_R1(1_17_0),
    MC1_18_R1(1_18_0, true),
    MC1_18_R2(1_18_2, true),
    MC1_19_R1(1_19_1, true),
    MC1_19_R2(1_19_2, true),
    MC1_19_R3(1_19_4, true),
    MC1_20_R1(1_20_1, true),
    MC1_20_R2(1_20_2, true),
    MC1_20_R3(1_20_3, true),
    MC1_20_R4(1_20_4, true);

    private final int versionId;
    public final boolean mojangMapping;

    MinecraftVersion(int versionId) {
        this(versionId, false);
    }

    MinecraftVersion(int versionId, boolean _mojangMapping) {
        this.versionId = versionId;
        this.mojangMapping = _mojangMapping;
    }

    private static MinecraftVersion version;
    private static Boolean hasGsonSupport;
    private static Boolean isPaper;

    /**
     * Gets & sets the Minecraft version the server is running.
     * If this has already been run, it will return the stored value instead of recalculating it.
     */
    public static MinecraftVersion init() {
        if (version != null) {
            return version;
        }
        final String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        isPaper = Package.getPackage("com.destroystokyo.paper") != null;
        try {
            version = MinecraftVersion.valueOf(ver.replace("v", "MC"));
        } catch (IllegalArgumentException ex) {
            version = MinecraftVersion.Unknown;
        }
        if (version == Unknown) {
            Bukkit.getServer().getLogger().warning("You are using invalid version of Minecraft [" + ver + "]! Disabling...");
        }
        return version;
    }

    /**
     * @return True if the server's Minecraft version is 1.13 or newer, false otherwise.
     */
    public static boolean isNew() {
        return getVersionNumber() >= 1_13_0;
    }

    public static boolean isPaper() {
        return isPaper;
    }

    public static MinecraftVersion getVersion() {
        return version;
    }

    /**
     * @return Integer representing the Minecraft version the server is running.
     */
    public static int getVersionNumber() {
        return version.versionId;
    }

    /**
     * @return The current MinecraftVersion the server is running.
     */
    public static MinecraftVersion getCurrentVersion() {
        return version;
    }


    /**
     * @return True if method names are in Mojang format and need to be remapped internally
     */
    public boolean isMojangMapping() {
        return mojangMapping;
    }

    public String getPackageName() {
        if (getVersion() == MinecraftVersion.Unknown) {
            return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }
        return this.name().replace("MC", "v");
    }


    /**
     * @return Integer representing the Minecraft version of the specific MinecraftVersion instance it's called from.
     */
    public int getVersionId() {
        return versionId;
    }

    /**
     * @return True if the server supports Gson, false otherwise.
     */
    public static boolean hasGsonSupport() {
        if (hasGsonSupport != null) {
            return hasGsonSupport;
        }
        try {
            hasGsonSupport = true;
        } catch (Exception ex) {
            hasGsonSupport = false;
        }
        return hasGsonSupport;
    }


    /**
     * Returns true if the current versions is at least the given Version
     *
     * @param version The minimum version
     * @return
     */
    public static boolean isAtLeastVersion(MinecraftVersion version) {
        return getVersion().getVersionId() >= version.getVersionId();
    }

    /**
     * Returns true if the current versions newer (not equal) than the given version
     *
     * @param version The minimum version
     * @return
     */
    public static boolean isNewerThan(MinecraftVersion version) {
        return getVersion().getVersionId() > version.getVersionId();
    }

    public static void disableDuplicateUUIDReporting(JavaPlugin plugin) {
//        FileConfiguration fc = plugin.getServer().spigot().getPaperConfig();
//        if (fc.contains("log-duplicate-entity-UUIDs"))
//            return;
//
//        fc.set("log-duplicate-entity-UUIDs", false);
//        File paperConfig = new File(plugin.getServer().getWorldContainer(), "paper.yml");
//
//        try {
//            fc.save(paperConfig);
//            plugin.getLogger().info("Disabled duplicate entity reporting ('log-duplicate-entity-UUIDs') in Paper.yml file...");
//        } catch (Exception ev) {
//            ev.printStackTrace();
//        }
    }
}