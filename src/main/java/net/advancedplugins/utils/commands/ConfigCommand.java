package net.advancedplugins.utils.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.advancedplugins.utils.DataHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public abstract class ConfigCommand<T extends CommandSender> extends SimpleCommand<T> {
    private final Config config;

    public ConfigCommand(JavaPlugin plugin, Config config, boolean isConsole) {
        super(plugin, config.command, config.permission, config.aliases, config.description, isConsole);
        this.config = config;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Config {
        private final String command;
        private final String permission;
        private final String description;
        private final List<String> aliases;
    }

    public static Config getConfig(DataHandler handler, String command) {
        return new Config(
                handler.getString(command + ".command"),
                handler.getString(command + ".permission"),
                handler.getString(command + ".description"),
                handler.getStringList(command + ".aliases")
        );
    }
}
