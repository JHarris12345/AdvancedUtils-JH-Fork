package net.advancedplugins.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.advancedplugins.utils.commands.argument.ArgumentHandler;
import net.advancedplugins.utils.commands.argument.ArgumentType;
import net.advancedplugins.utils.text.Text;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CommandBaseNew {
    private final JavaPlugin plugin;
    @Getter
    private final Set<ConfigCommand<? extends CommandSender>> commands = Sets.newHashSet();

    public CommandBaseNew(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registerArgumentTypes();
    }

    private static final Class<? extends Server> bukkitServerClass = Bukkit.getServer().getClass();

    public void registerCommand(ConfigCommand<? super CommandSender> cmd) {
        if (!cmd.getConfig().isEnabled()) return;
        try {
            final Field bukkitCommandMap = bukkitServerClass.getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            Command command = new Command(cmd.getConfig().getCommand()) {
                @Override
                public boolean execute(@NotNull CommandSender commandSender, @NotNull String string, @NotNull String[] strings) {
                    return onCommand(commandSender, this, string, strings);
                }

                @NotNull
                @Override
                public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
                    return onTabComplete(sender, this, alias, args);
                }
            };

            command.setAliases(cmd.getConfig().getAliases());
            if (cmd.getConfig().getDescription() != null)
                command.setDescription(cmd.getConfig().getDescription());
            command.setPermission(cmd.getConfig().getPermission());

            commandMap.register(cmd.getConfig().getCommand(), command);
            bukkitCommandMap.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.commands.add(cmd);
    }

    public CommandBaseNew registerArgumentType(Class<?> clazz, ArgumentType<?> argumentType) {
        ArgumentHandler.register(clazz, argumentType);
        return this;
    }

    public synchronized boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.Command command, @NotNull String label, String[] args) {
        String commandName = command.getName();
        for (ConfigCommand<? extends CommandSender> cmd : this.commands) {
            if (!cmd.getConfig().getCommand().equalsIgnoreCase(commandName)) {
                continue;
            }
            if (cmd.getConfig().getPermission() != null && !cmd.getConfig().getPermission().isEmpty() && !sender.hasPermission(cmd.getConfig().getPermission())) {
                Text.sendMessage(sender, cmd.getNoPermissionLang(sender));
                return true;
            }
            if (!cmd.isConsole() && sender instanceof ConsoleCommandSender) {
                sender.sendMessage("The console can not execute this command.");
                return true;
            }

            // Checks if the command has any sub commands
            if (cmd.getSubCommands().isEmpty() && cmd.getArgumentsSize() > args.length) {
                // If the command does not have sub commands, has arguments and the user didn't specify any, send the usage (ignores optional arguments)
                cmd.sendUsage(sender);
                return true;
            }

            if (args.length == 0) {
                cmd.middleMan(sender, args);
                return true;
            }

            ConfigSubCommand<? extends CommandSender> subResult = null;
            for (ConfigSubCommand<? extends CommandSender> subCommand : cmd.getSubCommands()) {
                if ((args.length > subCommand.getArgumentsSize() && subCommand.isEndless())
                        || (subCommand.getArgumentsSize() <= args.length && subCommand.isMatch(args)) ||
                        (args.length == subCommand.getArgumentsSizeReal() && subCommand.isMatch(args))) {
                    subResult = subCommand;
                    break;
                }
            }
            if (subResult == null) {
                cmd.middleMan(sender, args);
                return true;
            }
            if (subResult.getConfig().getPermission() != null && !sender.hasPermission(subResult.getConfig().getPermission()) && cmd.getConfig().getPermission() != null && !cmd.getConfig().getPermission().isEmpty()) {
                Text.sendMessage(sender, subResult.getNoPermissionLang(sender));
                return true;
            }
            if (!subResult.isConsole() && sender instanceof ConsoleCommandSender) {
                sender.sendMessage("The console can not execute this command.");
                return true;
            }
            subResult.middleMan(sender, args);
        }
        return true;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        List<String> tabCompleteSuggestions = Lists.newArrayList();
        String commandName = command.getName();
        for (ConfigCommand<? extends CommandSender> cmd : this.commands) {
            if (!cmd.getConfig().getCommand().equalsIgnoreCase(commandName)) {
                continue;
            }
            if (cmd.getConfig().getPermission() != null && !cmd.getConfig().getPermission().isEmpty() && !sender.hasPermission(cmd.getConfig().getPermission())) {
                continue;
            }
            if (!cmd.isConsole() && sender instanceof ConsoleCommandSender) {
                continue;
            }

            if (args.length == 0) {
                continue;
            }

            // Handle commands without any subcommands (only arguments)
            if (cmd.getSubCommands().isEmpty()) {
                // If the command does not have sub commands, has arguments and the user didn't specify any, send the usage (ignores optional arguments)
                tabCompleteSuggestions.addAll(cmd.tabCompletionSuggestion(sender, args.length - 1));
            } else {
                Set<ConfigSubCommand<? extends CommandSender>> subResults = Sets.newHashSet();
                for (ConfigSubCommand<? extends CommandSender> subCommand : cmd.getSubCommands()) {
                    if (subCommand.isMatchUntilIndex(args, args.length - 1)) {
                        subResults.add(subCommand);
                    }
                }
                if (subResults.isEmpty()) {
                    continue;
                }
                for (ConfigSubCommand<? extends CommandSender> subResult : subResults) {
                    if (subResult.getConfig().getPermission() != null && !sender.hasPermission(subResult.getConfig().getPermission()) && cmd.getConfig().getPermission() != null && !cmd.getConfig().getPermission().isEmpty()) {
                        continue;
                    }
                    if (!subResult.isConsole() && sender instanceof ConsoleCommandSender) {
                        continue;
                    }
                    tabCompleteSuggestions.addAll(subResult.tabCompletionSuggestion(sender, args.length - 1));
                }
            }
        }

        List<String> sortedArgs = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], tabCompleteSuggestions, sortedArgs);
        Collections.sort(sortedArgs);
        return sortedArgs;
    }

    private void registerArgumentTypes() {
        this.registerArgumentType(String.class, string -> string)
                .registerArgumentType(Player.class, Bukkit::getPlayerExact)
                .registerArgumentType(OfflinePlayer.class, Bukkit::getOfflinePlayer)
                .registerArgumentType(Integer.class, string -> NumberUtils.isNumber(string) ? Integer.parseInt(string) : 0)
                .registerArgumentType(Boolean.class, string -> string.equalsIgnoreCase("true") || (string.equalsIgnoreCase("false") ? false : null));
    }
}