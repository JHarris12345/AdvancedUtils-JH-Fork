package net.advancedplugins.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DataHandler {

    private File file = null;
    private FileConfiguration fileConfiguration;

    private String fileName;
    private int loopNumber;

    private static JavaPlugin instance;

    public DataHandler(File f, JavaPlugin plugin) {
        this.file = f;
        instance = plugin;
        populateFile(false);
    }

    public DataHandler(String fileName, JavaPlugin plugin) {
        this(fileName, plugin, false);
    }

    public DataHandler(String fileName, JavaPlugin plugin, boolean createNewFile) {
        instance = plugin;
        if (fileName == null) return;

        this.fileName = fileName;

        populateFile(createNewFile);
    }

    public DataHandler() {

    }

    private void populateFile(boolean createNewFile) {
        fileConfiguration = new YamlConfiguration();

        File folder = instance.getDataFolder();
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }

        if (file == null) {
            file = new File(instance.getDataFolder(), fileName + ".yml");
        }

        if (instance.getResource(fileName + ".yml") != null) {
            if (!file.exists()) {
                instance.saveResource(fileName + ".yml", true);
            }
        } else if (createNewFile) {
            String[] paths = fileName.split("/");
            String pastPath = "";

            if (paths.length > 1) {
                for (int i = 0; i < paths.length - 1; i++) {
                    pastPath += paths[i] + "/";

                    File path = new File(instance.getDataFolder(), pastPath);
                    if (!path.isDirectory()) {
                        path.mkdirs();
                    }
                }
            }


            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        file = new File(file.getPath());

        try {
            fileConfiguration = new YamlConfiguration();
            fileConfiguration.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
//        populateFile(false);
    }


    public FileConfiguration getConfig() {
        return fileConfiguration;
    }

    public File getFile() {
        return file;
    }

    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(instance, this::save);
    }

    public int increaseLoop() {
        loopNumber++;

        return loopNumber;
    }

    public int getLoopNumber() {
        return loopNumber;
    }

    public void clearLoopNumer() {
        loopNumber = 0;
    }

    public boolean isPath(String path) {
        return fileConfiguration.isConfigurationSection(path);
    }

    public Set<String> getKeys(String path) {
        if (!fileConfiguration.isConfigurationSection(path)) return Collections.emptySet();

        return fileConfiguration.getConfigurationSection(path).getKeys(false);
    }

    public Set<String> getKeys(FileConfiguration fc, String path) {
        return fc.getConfigurationSection(path).getKeys(false);
    }

    public <T extends Enum<T>> T getEnum(final String path, final Class<T> enumClass) {
        String data = fileConfiguration.getString(path);
        T finalEnum;

        try {
            finalEnum = Enum.valueOf(enumClass, data);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        return finalEnum;
    }

    public LocalLocation getLocation(String path) {
        String encoded = getConfig().getString(path);
        return LocalLocation.getFromEncode(encoded);
    }

    public void setLocation(String path, Location key) {
        setLocation(path, new LocalLocation(key));
    }

    public void setLocation(String path, LocalLocation key) {
        getConfig().set(path, key.getEncode());
    }

    public void tick() {
    }

    public void unload() {
        for (int id : activeTasks) {
            Bukkit.getScheduler().cancelTask(id);
        }

        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }

    public UUID stringToId(String input) {
        return UUID.fromString(input);
    }

    public UUID getUUID(String path) {
        return UUID.fromString(getConfig().getString(path));
    }

    public int getInt(String s) {
        return getConfig().getInt(s);
    }

    public List<String> getStringList(String s) {
        return getConfig().getStringList(s);
    }

    public String getString(String s) {
        return getConfig().getString(s);
    }

    public String getString(String s, String def) {
        return getConfig().getString(s, def);
    }

    public String getStringColored(String s) {
        return ColorUtils.format(getString(s));
    }

    public boolean getBoolean(String path, boolean def) {
        return getConfig().getBoolean(path, def);
    }

    public boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    public <T> HashMap<String, T> sectionToMap(String s, Class<T> type) {
        HashMap<String, T> map = new HashMap<>();
        for (String key : getKeys(s)) {
            map.put(key, (T) getConfig().get(s + "." + key));
        }
        return map;
    }

    public boolean isEnabled() {
        return getBoolean("enabled", true);
    }

    @Getter
    private List<Integer> activeTasks = new ArrayList<>();
    @Getter
    private List<Listener> listeners = new ArrayList<>();

    public void addTask(int id) {
        this.activeTasks.add(id);
    }

    public void registerListener(Listener l) {
        Bukkit.getPluginManager().registerEvents(l, ASManager.getInstance());
        this.listeners.add(l);
    }
}
