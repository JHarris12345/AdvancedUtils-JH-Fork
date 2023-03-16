package net.advancedplugins.utils.commands.compact;

import com.google.common.collect.Sets;
import net.advancedplugins.utils.commands.SubCommand;
import net.advancedplugins.utils.commands.argument.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public class SubChain<T extends CommandSender> {
    private LinkedList<SubCommand<? extends CommandSender>> subCommands = new LinkedList<>();

    public LinkedList<SubCommand<? extends CommandSender>> getSubCommands() {
        return this.subCommands;
    }

    public SubChain<T> newSub(JavaPlugin plugin, String permission, boolean isConsole, UnaryOperator<ArgumentBuilder> builder, Executor executor) {
        List<Argument<?>> arguments = builder.apply(new ArgumentBuilder()).getArguments();
        SubCommand<T> subCommand = new SubCommand<T>(plugin, permission, isConsole) {

            @Override
            public void onExecute(CommandSender sender, String[] args) {
                executor.execute(sender, args, null);
            }
        };
        subCommand.setArguments(arguments);
        this.subCommands.add(subCommand);
        return this;
    }

    public SubChain<T> newSub(JavaPlugin plugin, UnaryOperator<ArgumentBuilder> builder, Executor executor) {
        return this.newSub(plugin, "", true, builder, executor);
    }

    public SubChain<T> newSub(JavaPlugin plugin, String permission, UnaryOperator<ArgumentBuilder> builder, Executor executor) {
        return this.newSub(plugin, permission, true, builder, executor);
    }

    public SubChain<T> newSub(JavaPlugin plugin, boolean isConsole, UnaryOperator<ArgumentBuilder> builder, Executor executor) {
        return this.newSub(plugin, "", isConsole, builder, executor);
    }
}
