package net.advancedplugins.utils.locale.subclass;

import net.advancedplugins.utils.locale.LocaleHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LocaleFile {

    private File file;
    private final String locale;

    private FileConfiguration configuration = null;

    public LocaleFile(String locale, JavaPlugin plugin) {
        this.locale = locale;
        saveFile(plugin);
    }

    public String getLocale() {
        return locale;
    }

    public FileConfiguration getLocaleConfig() {
        if (configuration == null) {

            try {

                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr);

                configuration = YamlConfiguration.loadConfiguration(reader);
            } catch (Exception ev) {
                LocaleHandler.getHandler().getInstance().getLogger().warning("Failed to load locale " + locale);
                ev.printStackTrace();
                return null;
            }
        }

        return configuration;
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    private void saveFile(JavaPlugin plugin) {
        File dir = new File(plugin.getDataFolder(), "lang/");
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        this.file = new File(plugin.getDataFolder(), "lang/" + this.locale + ".yml");
        if (!file.exists()) {
            InputStream tempStream = plugin.getResource("lang/" + this.locale + ".yml");
            try {
                file.createNewFile();

                byte[] buffer = new byte[tempStream.available()];
                tempStream.read(buffer);

                OutputStream outStream = new FileOutputStream(this.file);
                outStream.write(buffer);
            } catch (Exception ev) {
                ev.printStackTrace();
            }
        }
    }
}
