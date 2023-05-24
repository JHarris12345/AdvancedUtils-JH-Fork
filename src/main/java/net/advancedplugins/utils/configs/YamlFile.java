package net.advancedplugins.utils.configs;

import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.utils.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public class YamlFile {

    public static final YamlFile ANVIL = new YamlFile("anvil.yml");
    public static final YamlFile CONFIG = new YamlFile("config.yml");
    public static final YamlFile ENCHANTMENTS = new YamlFile("enchantments.yml", false);
    public static final YamlFile MOB_HEADS = new YamlFile("mobHeads.yml");
    public static final YamlFile MOBS = new YamlFile("mobs.yml");
    public static final YamlFile PDATA = new YamlFile("pdata.yml");

    public static final YamlFile TINKERER = new YamlFile("menus/tinkerer.yml");
    public static final YamlFile ALCHEMIST = new YamlFile("menus/alchemist.yml");
    public static final YamlFile ENCHANTER = new YamlFile("menus/enchanter.yml");
    public static final YamlFile GKITS = new YamlFile("menus/gkits.yml");
    public static final YamlFile ASETS_PREVIEW = new YamlFile("menus/armorSetsPreview.yml");
    public static final YamlFile COMMANDS = new YamlFile("menus/customcommands.yml");

    private final File file;
    private YamlConfiguration cfg;
    private boolean autoUpdate = true;

    /**
     * Creates a new DataHandler.
     *
     * @param name Name of the config file. Doesn't need to end with ".yml" but can.
     */
    public YamlFile(String name) {
        assert name != null : "File name cannot be null!";
        name = name.endsWith(".yml") ? name : name + ".yml";
        name = name.replace("/", File.separator);
        file = new File(ASManager.getInstance().getDataFolder() + File.separator + name);
        writeFileIfNotExists();
        reload();
    }

    /**
     * Creates a new DataHandler.
     *
     * @param name       Name of the config file. Doesn't need to end with ".yml" but can.
     * @param autoUpdate Toggles whether or not defaults that don't exist in the file should be writtin to disk.
     */
    public YamlFile(String name, boolean autoUpdate) {
        this(name);
        this.autoUpdate = autoUpdate;
    }

    /**
     * Creates a new DataHandler.
     *
     * @param file File of the config.
     */
    public YamlFile(File file) {
        this.file = file;
        file.getParentFile().mkdirs();
        writeFileIfNotExists();
        reload();
    }


    /**
     * Writes the internal config to disk if it doesn't exist.
     */
    private void writeFileIfNotExists() {
        if (file.exists()) return;
        String from = getInternalName(file);
        try (InputStream is = ASManager.getInstance().getResource(from)) {
            if (is != null)
                ASManager.getInstance().saveResource(from, true);
                // why did we use proprietary system to save files?
//                writeToFile(ASManager.getInstance().getResource(from));
            else
                file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes an InputStream to a file.
     *
     * @param is InputStream to write.
     */
    private synchronized void writeToFile(InputStream is) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int i;
            StringBuilder fullMessage = new StringBuilder();
            if (is == null) return;
            while ((i = is.read()) != -1)
                fullMessage.append((char) i);
            byte[] strToBytes = fullMessage.toString().getBytes();
            outputStream.write(strToBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The File of the config.
     */
    public File getFile() {
        return file;
    }

    /**
     * Writes the config to disk.
     */
    public synchronized void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads the config from disk.
     */
    public synchronized void reload() {
        if (!file.exists()) writeFileIfNotExists();
        try {
            cfg = new YamlConfiguration();
            cfg.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } catch (IOException | InvalidConfigurationException e) {
            autoUpdate = false;
            String from = getInternalName(file);
            ASManager.getInstance().getLogger().severe("Failed to load config file \"" + from + "\"! Please ensure that it doesn't have any syntax errors. " +
                    "You can check for syntax errors with this website: \"https://www.yamlchecker.com/\". " +
                    "If you see any errors this, this is most likely the cause of them.");
            e.printStackTrace();
            try (InputStream is = ASManager.getInstance().getResource(from)) {
                if (is != null) {
                    cfg.load(new InputStreamReader(is));
                }
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * Gets the internal file name of a file.
     *
     * @param file File to get internal name of.
     * @return Internal name of a file. (eg. "armorSets/Koth.yml").
     */
    private String getInternalName(File file) {
        return file.getAbsolutePath().replace(ASManager.getInstance().getDataFolder().getAbsolutePath() + File.separator, "").replace(File.separatorChar, '/');
    }


    //<editor-fold desc="Getters/ Setters">

    /**
     * Checks if a path exists in the config.
     *
     * @param path Path to check.
     * @return True if the path exists, false otherwise.
     */
    public boolean contains(String path) {
        return cfg.contains(path);
    }

    /**
     * Checks if a path is a {@link ConfigurationSection} in the config.
     *
     * @param path Path to check.
     * @return True if the path exists and is a ConfigurationSection, false otherwise.
     */
    public boolean isConfigSection(String path) {
        return cfg.isConfigurationSection(path);
    }

    /**
     * Gets all keys (non-nested) of a ConfigurationSection.
     *
     * @param path Path to get keys from.
     * @return List of keys.
     */
    public Set<String> getKeys(String path) {
        return getConfigSection(path).getKeys(false);
    }

    /**
     * Gets a {@link Object} from the config.
     *
     * @param path Path of the Object.
     * @return The Object from the config.
     */
    public Object get(String path) {
        return cfg.get(path);
    }

    /**
     * Gets a {@link Object} from the config, or writes the default if it doesn't exist.
     *
     * @param path Path of the Object.
     * @param def  Object to write if it doesn't exist.
     * @return The Object from the config, or the default if it doesn't exist.
     */
    public Object get(String path, Object def) {
        update(path, def);
        return cfg.get(path, def);
    }

    /**
     * Gets a {@link Location} from the config.
     *
     * @param path Path of the Location.
     * @return The Location as stored in the config.
     */
    public Location getLocation(String path) {
        if (MinecraftVersion.getVersionNumber() >= 1_14_0)
            return cfg.getLocation(path);
        String[] data = getString(path).split(";");
        World world = Bukkit.getWorld(data[0]);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);
        return new Location(world, x, y, z);
    }

    /**
     * Gets a {@link Integer} from the config.
     *
     * @param path Path of the Integer.
     * @return The Integer from the config.
     */
    public int getInt(String path) {
        return cfg.getInt(path);
    }

    /**
     * Gets a {@link Integer} from the config, or writes the default if it doesn't exist.
     *
     * @param path Path of the Integer.
     * @param def  Integer to write if it doesn't exist.
     * @return The Integer from the config, or the default if it doesn't exist.
     */
    public int getInt(String path, int def) {
        update(path, def);
        return cfg.getInt(path, def);
    }

    /**
     * Gets a {@link Long} from the config.
     *
     * @param path Path of the Long.
     * @return The Long from the config.
     */
    public long getLong(String path) {
        return cfg.getLong(path);
    }

    /**
     * Gets a {@link Long} from the config, or writes the default if it doesn't exist.
     *
     * @param path Path of the Long.
     * @param def  Long to write if it doesn't exist.
     * @return The Long from the config, or the default if it doesn't exist.
     */
    public long getLong(String path, long def) {
        update(path, def);
        return cfg.getLong(path, def);
    }

    /**
     * Gets a {@link Double} from the config.
     *
     * @param path Path of the Double.
     * @return The Double from the config.
     */
    public double getDouble(String path) {
        return cfg.getDouble(path);
    }

    /**
     * Gets a {@link Double} from the config, or writes the default if it doesn't exist.
     *
     * @param path Path of the Double.
     * @param def  Double to write if it doesn't exist.
     * @return The Double from the config, or the default if it doesn't exist.
     */
    public double getDouble(String path, double def) {
        update(path, def);
        return cfg.getDouble(path, def);
    }

    /**
     * Gets a {@link Boolean} from the config.
     *
     * @param path Path of the Boolean.
     * @return The Boolean from the config.
     */
    public boolean getBoolean(String path) {
        return cfg.getBoolean(path);
    }

    /**
     * Gets a {@link Boolean} from the config, or writes the default if it doesn't exist.
     *
     * @param path Path of the Boolean.
     * @param def  Boolean to write if it doesn't exist.
     * @return The Boolean from the config, or the default if it doesn't exist.
     */
    public boolean getBoolean(String path, boolean def) {
        update(path, def);
        return cfg.getBoolean(path, def);
    }

    /**
     * Gets a {@link String} from the config.
     *
     * @param path Path of the String.
     * @return The String from the config.
     */
    public String getString(String path) {
        return Text.modify(cfg.getString(path));
    }

    /**
     * Gets a {@link String} from the config, or writes the default if it doesn't exist.
     *
     * @param path Path of the String.
     * @param def  String to write if it doesn't exist.
     * @return The String from the config, or the default if it doesn't exist.
     */
    public String getString(String path, String def) {
        update(path, def);
        return Text.modify(cfg.getString(path, def));
    }

    /**
     * Gets a {@link List<String>} from the config.
     *
     * @param path Path of the List.
     * @return The List from the config.
     */
    public List<String> getStringList(String path) {
        return Text.modify(cfg.getStringList(path));
    }

    /**
     * Gets a {@link List<String>} from the config, or writes the default if it doesn't exist.
     *
     * @param path Path of the List.
     * @param def  List to write if it doesn't exist.
     * @return The List from the config, or the default if it doesn't exist.
     */
    public List<String> getStringList(String path, List<String> def) {
        update(path, def);
        return Text.modify(cfg.getStringList(path));
    }

    /**
     * Gets a {@link ConfigurationSection} from the config.
     *
     * @param path Path of the ConfigurationSection.
     * @return The ConfigurationSection.
     */
    public ConfigurationSection getConfigSection(String path) {
        return cfg.getConfigurationSection(path);
    }

    /**
     * Writes a value to the file if it doesn't exist.
     *
     * @param path Path to put the value.
     * @param def  The value to put.
     */
    private void update(String path, Object def) {
        if (!contains(path)) {
            set(path, def);
            if (autoUpdate) {
                save();
            }
        }
    }

    /**
     * Writes an {@link Object} to the config.
     *
     * @param path Path to put the Object.
     * @param obj  Object to put.
     */
    public void set(String path, Object obj) {
        cfg.set(path, obj);
    }

    /**
     * @return The {@link YamlConfiguration} of the config.
     */
    public YamlConfiguration getConfig() {
        return cfg;
    }
    //</editor-fold>
}
