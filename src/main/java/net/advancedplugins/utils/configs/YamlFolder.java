package net.advancedplugins.utils.configs;


import net.advancedplugins.utils.ASManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Wrapper class that stores nested {@link YamlFile}s in a folder.
 */
public class YamlFolder {

    public static final YamlFolder ARMOR_SETS = new YamlFolder("armorSets");
    public static final YamlFolder CUSTOM_WEAPONS = new YamlFolder("customWeapons");

    private final Map<String, YamlFile> dataFiles = new HashMap<>();
    private final String folder;

    /**
     * Creates a new YamlFolder.
     *
     * @param folder Name of the root folder.
     */
    public YamlFolder(String folder) {
        this.folder = folder;
        File dir = ASManager.getInstance().getDataFolder();
        File file = new File(dir.getAbsolutePath() + File.separator + folder);
        boolean exists = true;
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
            exists = false;
        }
        initExisting();
        if (!exists)
            writeToDisk();
    }

    /**
     * Writes all sub-files from the jar to disk.
     */
    private void writeToDisk() {
        try {
            CodeSource src = ASManager.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                try (ZipInputStream zip = new ZipInputStream(jar.openStream())) {
                    while (true) {
                        ZipEntry e = zip.getNextEntry();
                        if (e == null)
                            break;
                        String name = e.getName();
                        if (!isYamlFile(name)) continue;
                        if (!name.startsWith(folder)) continue;
                        if (!name.contains("/")) continue;
                        File file = new File(ASManager.getInstance().getDataFolder(), name);
                        if (file.isDirectory()) continue;
                        name = name.replace("/", File.separator);
                        dataFiles.put(name, new YamlFile(file));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates YamlFile's for files that already exist in the directory.
     */
    private void initExisting() {
        for (File sub : getNestedFiles(new File(ASManager.getInstance().getDataFolder(), folder))) {
            if (!isYamlFile(sub.getName()))
                continue;
            String fileName = sub.getPath().replace(ASManager.getInstance().getDataFolder().getPath() + File.separator, "");
            dataFiles.put(fileName, new YamlFile(sub));
        }
    }

    /**
     * Checks if a file's extension is ".yml".
     *
     * @param fileName Name of file to check.
     * @return True if the files extension is ".yml", false otherwise.
     */
    private boolean isYamlFile(String fileName) {
        if (!fileName.contains("."))
            return false;
        return fileName.substring(fileName.lastIndexOf('.')).toLowerCase(Locale.ROOT).equals(".yml");
    }

    /**
     * Gets a {@link YamlFile} from a file name.
     *
     * @param name Name of config file to get (with or without .yml).
     * @return DataFile associated with that file.
     */
    public YamlFile getDataFile(String name) {
        name = name.replace("/", File.separator);
        name = (name.startsWith(folder)) ? name : folder + File.separator + name;
        name = (name.endsWith(".yml")) ? name : name + ".yml";
        return dataFiles.get(name);
    }

    /**
     * Gets a {@link YamlFile} from a file.
     *
     * @param file File to get.
     * @return DataFile associated with that file.
     */
    public YamlFile getDataFile(File file) {
        String fileName = file.getPath().replace(ASManager.getInstance().getDataFolder().getPath() + File.separator, "");
        return getDataFile(fileName);
    }

    /**
     * @return Collection of all {@link YamlFile}s in this DataFolder.
     */
    public Collection<YamlFile> getDataFiles() {
        return dataFiles.values();
    }

    /**
     * Gets all nested files in a folder.
     *
     * @param root Root folder to get all nested files from.
     * @return List of all nested files.
     */
    private static List<File> getNestedFiles(File root) {
        List<File> fileNames = new ArrayList<>();
        for (File f : root.listFiles()) {
            if (f.isDirectory()) {
                fileNames.addAll(getNestedFiles(f));
                continue;
            }
            fileNames.add(f);
        }
        return fileNames;
    }

}
