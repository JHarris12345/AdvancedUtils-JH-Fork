package net.advancedplugins.utils.commands.argument;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

@Getter
public class Argument<T> {
    /**
     * -- GETTER --
     *  Gets the type of the Argument.
     *
     * @return The ArgumentType of the class
     */
    private final ArgumentType<T> type;
    /**
     * -- GETTER --
     *  Gets the argument of the class.
     *
     * @return The argument of the class, e.g "player" or "ban"
     */
    private final String argument;
    /**
     * -- GETTER --
     *  Gets the aliases, will return an empty hash set if there are none.
     *
     * @return The aliases of the command.
     */
    private final Set<String> aliases;
    /**
     * -- GETTER --
     *  Gets the function for what strings are to be
     *  used on tab complete for this argument
     *
     * @return The function
     */
    private final Function<CommandSender, Collection<String>> onTabComplete;
    private  boolean optional;

    /**
     * Creates an argument for use with a SimpleCommand.
     *
     * @param type          The clazz type of the argument, e.g a Player, OfflinePlayer, Integer or User.
     * @param argument      The type of argument (used in help), e.g player or amount.
     * @param onTabComplete The list of strings that are suggested on tab complete
     */
    public Argument(ArgumentType<T> type, String argument, Function<CommandSender, Collection<String>> onTabComplete, String... aliases) {
        this.type = type;
        this.argument = argument;
        this.aliases = Sets.newHashSet(aliases);
        this.onTabComplete = onTabComplete;
    }

    /**
     * Creates an argument for use with a SimpleCommand which has aliases.
     *
     * @param type     The clazz type of the argument, e.g a Player, OfflinePlayer, Integer or User.
     * @param argument The type of argument (used in help), e.g player or amount.
     * @param aliases  The alternatives (aliases) that can be used.
     */
    public Argument(ArgumentType<T> type, String argument, String... aliases) {
        this(type, argument, sender -> Lists.newArrayList(argument), aliases);
    }

    public Argument asOptional() {
        this.optional = true;
        return this;
    }

}