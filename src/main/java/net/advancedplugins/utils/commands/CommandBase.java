package net.advancedplugins.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.advancedplugins.utils.commands.argument.ArgumentHandler;
import net.advancedplugins.utils.commands.argument.ArgumentType;
import net.advancedplugins.utils.text.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class CommandBase implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    @Getter
    private Set<SimpleCommand<? extends CommandSender>> commands = Sets.newHashSet();

    public CommandBase(JavaPlugin plugin) {
        this.plugin = plugin;
        this.registerArgumentTypes();
    }

    public void registerCommand(SimpleCommand<? super CommandSender> command) {
        PluginCommand pluginCommand = this.plugin.getCommand(command.getCommand());
        if (pluginCommand == null) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to load the command " + command.getCommand());
            return;
        }
        pluginCommand.setExecutor(this);
        this.commands.add(command);
    }

    public CommandBase registerArgumentType(Class<?> clazz, ArgumentType<?> argumentType) {
        ArgumentHandler.register(clazz, argumentType);
        return this;
    }

    @Override
    public synchronized boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        String commandName = command.getName();
        for (SimpleCommand<? extends CommandSender> simpleCommand : this.commands) {
            if (!simpleCommand.getCommand().equalsIgnoreCase(commandName)) {
                continue;
            }
            if (simpleCommand.getPermission() != null && !simpleCommand.getPermission().isEmpty() && !sender.hasPermission(simpleCommand.getPermission())) {
                Text.sendMessage(sender, simpleCommand.getNoPermissionLang(sender));
                return true;
            }
            if (!simpleCommand.isConsole() && sender instanceof ConsoleCommandSender) {
                sender.sendMessage("The console can not execute this command.");
                return true;
            }

            // Checks if the command has any sub commands
            if (simpleCommand.getSubCommands().isEmpty() && simpleCommand.getArgumentsSize() > args.length) {
                // If the command does not have sub commands, has arguments and the user didn't specify any, send the usage (ignores optional arguments)
                simpleCommand.sendUsage(sender);
                return true;
            }

            if (args.length == 0) {
                simpleCommand.middleMan(sender, args);
                return true;
            }

            SubCommand<? extends CommandSender> subResult = null;
            for (SubCommand<? extends CommandSender> subCommand : simpleCommand.getSubCommands()) {
                if ((args.length > subCommand.getArgumentsSize() && subCommand.isEndless())
                        || (subCommand.getArgumentsSize() <= args.length && subCommand.isMatch(args))) {
                    subResult = subCommand;
                    break;
                }
            }
            if (subResult == null) {
                simpleCommand.middleMan(sender, args);
                return true;
            }
            if (!subResult.doesInheritPermission() && subResult.getPermission() != null && !sender.hasPermission(subResult.getPermission()) && !simpleCommand.getPermission().isEmpty()) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabCompleteSuggestions = Lists.newArrayList();
        String commandName = command.getName();
        for (SimpleCommand<? extends CommandSender> simpleCommand : this.commands) {
            if (!simpleCommand.getCommand().equalsIgnoreCase(commandName)) {
                continue;
            }
            if (simpleCommand.getPermission() != null && !simpleCommand.getPermission().isEmpty() && !sender.hasPermission(simpleCommand.getPermission())) {
                continue;
            }
            if (!simpleCommand.isConsole() && sender instanceof ConsoleCommandSender) {
                continue;
            }
            if (args.length == 0) {
                continue;
            }

            // Handle commands without any subcommands (only arguments)
            if (simpleCommand.getSubCommands().isEmpty()) {
                // If the command does not have sub commands, has arguments and the user didn't specify any, send the usage (ignores optional arguments)
                tabCompleteSuggestions.addAll(simpleCommand.tabCompletionSuggestion(sender, args.length - 1));
            } else {
                Set<SubCommand<? extends CommandSender>> subResults = Sets.newHashSet();
                for (SubCommand<? extends CommandSender> subCommand : simpleCommand.getSubCommands()) {
                    if (subCommand.isMatchUntilIndex(args, args.length - 1)) {
                        subResults.add(subCommand);
                    }
                }
                if (subResults.isEmpty()) {
                    continue;
                }
                for (SubCommand<? extends CommandSender> subResult : subResults) {
                    if (!subResult.doesInheritPermission() && subResult.getPermission() != null && !sender.hasPermission(subResult.getPermission()) && simpleCommand.getPermission() != null && !simpleCommand.getPermission().isEmpty()) {
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
                .registerArgumentType(Integer.class, string -> StringUtils.isNumeric(string) ? Integer.parseInt(string) : 0)
                .registerArgumentType(Boolean.class, string -> string.equalsIgnoreCase("true") || (string.equalsIgnoreCase("false") ? false : null));
    }
}