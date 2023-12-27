package net.advancedplugins.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.advancedplugins.utils.commands.argument.Argument;
import net.advancedplugins.utils.commands.argument.ArgumentHandler;
import net.advancedplugins.utils.commands.argument.ArgumentType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class SubCommand<T extends CommandSender> extends Command<T> {
    private final boolean endless;
    private List<Argument<?>> arguments = Lists.newArrayList();
    private boolean inheritPermission;
    private String description;

    public SubCommand(JavaPlugin plugin, String permission, boolean isConsole) {
        this(plugin, permission, isConsole, false);
    }

    public SubCommand(JavaPlugin plugin, String permission, boolean isConsole, boolean endless) {
        super(plugin, permission, isConsole);
        this.endless = endless;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public SubCommand(JavaPlugin plugin) {
        this(plugin, "", true);
    }

    public SubCommand(JavaPlugin plugin, String permission) {
        this(plugin, permission, true);
    }

    public SubCommand(JavaPlugin plugin, boolean isConsole) {
        this(plugin, "", isConsole);
    }

    protected void inheritPermission() {
        this.inheritPermission = true;
    }

    public boolean doesInheritPermission() {
        return this.inheritPermission;
    }

    public boolean isEndless() {
        return this.endless;
    }

    public void setArguments(List<Argument<?>> arguments) {
        this.arguments = arguments;
    }

    public void addFlat(String flat) {
        this.arguments.add(new Argument<>(null, flat));
    }

    public void addFlatWithAliases(String flat, String... aliases) {
        this.arguments.add(new Argument<>(null, flat, aliases));
    }

    public void addFlats(String... flat) {
        for (String flatArgument : flat) {
            this.addFlat(flatArgument);
        }
    }

    protected <S> Argument<S> addArgument(Class<S> clazz, String argument, String... aliases) {
        if (argument.equalsIgnoreCase("player")) {
            return addArgument(clazz, argument, null, aliases);
        }
        Argument<S> a = new Argument<S>(ArgumentHandler.getArgumentType(clazz), argument, aliases);
        this.arguments.add(a);
        return a;
    }

    protected <S> Argument<S> addArgument(Class<S> clazz, String argument, Function<CommandSender, List<String>> onTabComplete, String... aliases) {
        if (argument.equalsIgnoreCase("player")) {
            onTabComplete = sender -> Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        Argument<S> a = new Argument<>(ArgumentHandler.getArgumentType(clazz), argument, onTabComplete, aliases);
        this.arguments.add(a);
        return a;
    }

    public int getArgumentsSize() {
        return (int) this.arguments.stream().filter(s -> !s.isOptional()).count();
    }

    public int getArgumentsSizeReal() {
        return this.arguments.size();
    }

    @SuppressWarnings("unchecked")
    public <U> U parseArgument(String[] args, int index) {
        return parseArgument(args, index, null);
    }

    public <U> U parseArgument(String[] args, int index, U def) {
        String arg = args.length - 1 < index ? null : args[index];
        if (arg == null)
            return def;
        return ((Argument<U>) this.arguments.get(index)).getType().parse(arg);
    }

    public <U> U parseArgument(String[] args, int index, Supplier<U> def) {
        String arg = args.length - 1 < index ? null : args[index];
        if (arg == null)
            return def == null ? null : def.get();
        return ((Argument<U>) this.arguments.get(index)).getType().parse(arg);
    }

    public boolean isMatch(String[] args) {
        return this.isMatchUntilIndex(args, args.length);
    }

    public String[] getEnd(String[] arguments) {
        Set<String> newSet = Sets.newLinkedHashSet();
        for (int i = 0; i < arguments.length; i++) {
            if (i < this.arguments.size() - 1) {
                continue;
            }
            newSet.add(arguments[i]);
        }
        return newSet.toArray(new String[]{});
    }

    public boolean isMatchUntilIndex(String[] args, int index) {
        for (int i = 0; i < index; i++) {
            if (!this.isArgumentValid(args, i)) {
                return false;
            }
        }
        return true;
    }

    public List<String> tabCompletionSuggestion(CommandSender commandSender, int index) {
        if (index > this.arguments.size() - 1) {
            return Lists.newArrayList();
        }
        return this.arguments.get(index).getOnTabComplete().apply(commandSender);
    }

    private boolean isArgumentValid(String[] arguments, int index) {
        if (getArgumentsSize() - 1 < index && this.endless) {
            return true;
        }
        Argument<?> argument = this.arguments.get(index);
        if (argument.getType() == null) {
            String matchTo = arguments[index];
            for (String alias : argument.getAliases()) {
                if (matchTo.equalsIgnoreCase(alias)) {
                    return true;
                }
            }
            return arguments[index].equalsIgnoreCase(argument.getArgument());
        }
        return true;
    }

    public String getFormatted(String command) {
        StringBuilder builder = new StringBuilder().append("/").append(command).append(" ");
        for (Argument arg : this.arguments) {
            if (arg.getType() == null) {
                builder.append(arg.getArgument());
            } else {
                if (arg.isOptional())
                    builder.append("&9[").append(arg.getArgument()).append("]&r");
                else
                    builder.append("&2<").append(arg.getArgument()).append(">&r");
            }
            builder.append(" ");
        }

        builder.append("&8-&e ");
        builder.append(getDescription());
        return builder.toString();
    }
}