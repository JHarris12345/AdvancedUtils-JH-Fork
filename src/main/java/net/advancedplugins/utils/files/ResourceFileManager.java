package net.advancedplugins.utils.files;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.security.CodeSource;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceFileManager {
    /**
     * Recursively saves all files from a specified resource folder to the plugin's data folder,
     * maintaining the original folder structure
     *
     * @param plugin The JavaPlugin instance
     * @param resourceFolder The folder path within resources to save from (e.g., "lang" or "data/configs")
     * @param fileExtension Optional file extension filter (e.g., ".yml"). Pass null to save all files
     * @throws IOException If there's an error accessing or saving files
     */
    public static void saveAllResources(JavaPlugin plugin, String resourceFolder, String fileExtension) {
        try {
            // Create base directory
            File dataFolder = new File(plugin.getDataFolder(), resourceFolder);
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            // Get CodeSource from plugin's class
            CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
            if (src == null) {
                plugin.getLogger().warning("Could not get CodeSource for plugin");
                return;
            }

            // Open JAR file as ZIP stream
            try (ZipInputStream zip = new ZipInputStream(src.getLocation().openStream())) {
                ZipEntry entry;
                int filesProcessed = 0;

                // Iterate through JAR contents
                while ((entry = zip.getNextEntry()) != null) {
                    String name = entry.getName();

                    // Check if file is in the target folder
                    if (name.startsWith(resourceFolder + "/")) {
                        String relativePath = name.substring(resourceFolder.length() + 1);

                        // Apply file extension filter if specified
                        if (fileExtension == null || name.endsWith(fileExtension)) {
                            File targetFile = new File(dataFolder, relativePath);

                            // Create parent directories if needed
                            if (!targetFile.exists()) {
                                targetFile.getParentFile().mkdirs();
                                plugin.saveResource(name, false);
                                filesProcessed++;
                            }
                        }
                    }
                }

                if (filesProcessed == 0) {
                    plugin.getLogger().warning("No files found in: " + resourceFolder);
                }
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save resources from " + resourceFolder + ": " + e.getMessage());
        }
    }

    /**
     * Utility method to save specific file types from multiple resource folders
     *
     * @param plugin The JavaPlugin instance
     * @param folders Array of folder paths to process
     * @param fileExtension File extension to filter by (e.g., ".yml")
     */
    public static void saveAllResourceFolders(JavaPlugin plugin, String[] folders, String fileExtension) {
        for (String folder : folders) {
            try {
                saveAllResources(plugin, folder, fileExtension);
            } catch (Exception e) {
                plugin.getLogger().warning("Skipping folder '" + folder + "' due to error: " + e.getMessage());
            }
        }
    }
}