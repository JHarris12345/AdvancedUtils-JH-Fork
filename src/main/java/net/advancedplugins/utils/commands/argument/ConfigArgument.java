package net.advancedplugins.utils.commands.argument;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.advancedplugins.utils.commands.ConfigCommand;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.function.Function;

@Getter
public class ConfigArgument<T> extends Argument<T> {
    private final ConfigCommand.ArgConfig argConfig;

    /**
     * Creates an argument for use with a SimpleCommand.
     *
     * @param argConfig     The argument configuration.
     * @param type          The clazz type of the argument, e.g a Player, OfflinePlayer, Integer or User.
     * @param onTabComplete The list of strings that are suggested on tab complete
     */
    public ConfigArgument(ConfigCommand.ArgConfig argConfig, ArgumentType<T> type, Function<CommandSender, Collection<String>> onTabComplete, String... aliases) {
        super(type, argConfig.getDisplayArg(), onTabComplete, aliases);
        this.argConfig = argConfig;
    }

    /**
     * Creates an argument for use with a SimpleCommand which has aliases.
     *
     * @param argConfig The argument configuration.
     * @param type     The clazz type of the argument, e.g a Player, OfflinePlayer, Integer or User.
     * @param aliases  The alternatives (aliases) that can be used.
     */
    public ConfigArgument(ConfigCommand.ArgConfig argConfig, ArgumentType<T> type, String... aliases) {
        this(argConfig, type, sender -> Lists.newArrayList(argConfig.getDisplayArg()), aliases);
    }
}