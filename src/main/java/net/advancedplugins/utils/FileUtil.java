package net.advancedplugins.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {
    private static final String UPDATED_KEY = "last-config-update";

    public static void validateFile(String localPath, String resourcePath, Plugin plugin) throws IOException {
        File localFile = new File(plugin.getDataFolder(), localPath);

        if(!localFile.exists()) {
            plugin.saveResource(resourcePath,false);
            return;
        }

        YamlConfiguration localConfig = YamlConfiguration.loadConfiguration(localFile);
        YamlConfiguration resourceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(
                plugin.getResource(resourcePath)
        ));

        String crrVer = plugin.getDescription().getVersion();
        String configVer = localConfig.getString(UPDATED_KEY, "");

        if(crrVer.equalsIgnoreCase(configVer) || configVer.equalsIgnoreCase("OFF")) {
            return;
        }

        resourceConfig.getKeys(true).forEach(key -> {
            if(!localConfig.contains(key)) {
                localConfig.set(key, resourceConfig.get(key));
            }
        });
        localConfig.set(UPDATED_KEY, crrVer);
        localConfig.save(localFile);
        Bukkit.getLogger().info("["+plugin.getName()+"] Updated " + localPath + " to latest version");
    }
}
