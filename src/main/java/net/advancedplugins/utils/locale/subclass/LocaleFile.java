package net.advancedplugins.utils.locale.subclass;

import lombok.Getter;
import lombok.Setter;
import net.advancedplugins.utils.locale.LocaleHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LocaleFile {

    private File file;
    @Getter
    private final String locale;

    @Getter
    @Setter
    private FileConfiguration configuration = null;

    public LocaleFile(String locale, JavaPlugin plugin) {
        this.locale = locale;
        saveFile(plugin);
    }

    public void reloadLocaleConfig() {
        // because in getLocaleConfig() we check for null and create new instance if needed, this is enough to reload to config
        this.configuration = null;
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
