package net.advancedplugins.utils.locale;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.advancedplugins.utils.locale.subclass.LocaleFile;
import net.advancedplugins.utils.text.Text;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class LocaleHandler {

    private String locale;
    private String langFolder;
    private final JavaPlugin instance;

    private ImmutableMap<String, LocaleFile> localeMap;
    private static LocaleHandler handler = null;

    private String prefix;

    public LocaleHandler(JavaPlugin plugin) {
        this.instance = plugin;
        handler = this;
        localeMap = ImmutableMap.<String, LocaleFile>builder().build();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = color(getString(prefix));
    }

    public ImmutableSet<String> getAvailableLocales() {
        return localeMap.keySet();
    }

    public static LocaleHandler getHandler() {
        return handler;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;

        if (!localeMap.containsKey(locale)) {
            try {
                instance.saveResource(langFolder + "/" + locale + ".yml", true);
            } catch (Exception ignored) {
            }

            localeMap = ImmutableMap.<String, LocaleFile>builder()
                    .putAll(localeMap).put(locale, new LocaleFile(locale, instance)).build();
        }
    }

    public LocaleHandler readLocaleFiles(JavaPlugin plugin, String langFolder) {
        this.langFolder = langFolder;
        try {
            for (File f : new File(plugin.getDataFolder(), langFolder).listFiles()) {
                if (!f.getName().endsWith(".yml"))
                    continue;
                String locale = f.getName().replace(".yml", "");
                localeMap = ImmutableMap.<String, LocaleFile>builder()
                        .putAll(localeMap).put(locale, new LocaleFile(locale, instance)).build();
            }
            return this;
        } catch (Exception ev) {
            return this;
        }
    }

    private String color(String input) {
        return Text.modify(input);
    }

    public LocaleFile getFile() {
        return localeMap.get(locale);
    }

    public String getString(String path, String defaultValue) {
        return color(localeMap.get(locale).getLocaleConfig().getString(path, defaultValue)
                .replace("%prefix%", getPrefix() != null ? getPrefix() : ""));
    }

    public String getString(String path) {
        return color(localeMap.get(locale).getLocaleConfig().getString(path)
                .replace("%prefix%", getPrefix() != null ? getPrefix() : ""));
    }

    public List<String> getStringList(String path) {
        return localeMap.get(locale).getLocaleConfig().getStringList(path)
                .stream().map(this::color)
                .collect(Collectors.toList());
    }

    public JavaPlugin getInstance() {
        return instance;
    }

}
