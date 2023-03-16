package net.advancedplugins.utils.commands.compact;

import net.advancedplugins.utils.commands.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.UnaryOperator;

public abstract class CompactCommand<T extends CommandSender> extends SimpleCommand<T> {

    public CompactCommand(JavaPlugin plugin, String command, String permission, boolean isConsole) {
        super(plugin, command, permission, isConsole);
    }

    public CompactCommand(JavaPlugin plugin, String command, boolean isConsole) {
        super(plugin, command, isConsole);
    }

    public CompactCommand(JavaPlugin plugin, String command, String permission) {
        super(plugin, command, permission);
    }

    public CompactCommand(JavaPlugin plugin, String command) {
        super(plugin, command);
    }

    public void subChain(UnaryOperator<SubChain<? extends CommandSender>> operator) {
        SubChain<? extends CommandSender> subChain = operator.apply(new SubChain<T>());
        this.setSubCommands(subChain.getSubCommands());
    }
}
