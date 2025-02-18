package net.advancedplugins.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.advancedplugins.utils.commands.argument.Argument;
import net.advancedplugins.utils.commands.argument.ArgumentHandler;
import net.advancedplugins.utils.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class SimpleCommand<T extends CommandSender> extends Command<T> {
    @Getter
    private final String command;
    private Integer pageCount;

    private final int COMMANDS_PER_PAGE = 9;

    @Getter
    private LinkedList<SubCommand<? extends CommandSender>> subCommands = new LinkedList<>();
    @Getter
    private List<Argument<?>> arguments = new ArrayList<>();
    private LinkedList<ShowcaseCommand> showcaseCommands = new LinkedList<>();

    public SimpleCommand(JavaPlugin plugin, String command, String permission, boolean isConsole) {
        super(plugin, permission, isConsole);
        this.command = command;
    }

    public SimpleCommand(JavaPlugin plugin, String command, boolean isConsole) {
        this(plugin, command, "", isConsole);
    }

    public SimpleCommand(JavaPlugin plugin, String command, String permission) {
        this(plugin, command, permission, true);
    }

    public SimpleCommand(JavaPlugin plugin, String command) {
        this(plugin, command, true);
    }

    public void setSubCommands(LinkedList<SubCommand<? extends CommandSender>> subCommands) {
        this.subCommands = subCommands;
    }

    public void addShowcaseCommand(String name, String description) {
        showcaseCommands.add(new ShowcaseCommand(name, description));
    }

    protected void setSubCommands(SubCommand<? extends CommandSender>... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

    public void sendHelpMessage(Plugin plugin, CommandSender sendTo) {
        PluginDescriptionFile description = plugin.getDescription();
        Text.sendMessage(sendTo, "&f".concat(description.getName()).concat(" &7v").concat(description.getVersion()));
        Text.sendMessage(sendTo, "&7Use &f&n".concat(this.command).concat(" to view usage information."));
    }

    public void sendHelpPage(CommandSender sendTo, String color, String[] args) {
        int page = (args.length == 0 ? 0 : StringUtils.isNumeric(args[0]) ? Math.max(0, Integer.parseInt(args[0])) : 1) - 1;

        int subCount = subCommands.size();
        if (pageCount == null) pageCount = (int) Math.ceil((float) (subCount) / COMMANDS_PER_PAGE);
        page = Math.min(Math.max(0, page), pageCount);

        if (page + 1 > pageCount) {
            page = pageCount - 1;
        }

        PluginDescriptionFile description = super.plugin.getDescription();
        Text.sendMessage(sendTo, color + "[<] &8+-------< " + color + "&l" + description.getName().concat(" &7Page " + (page + 1) + "/" + pageCount) + " &8>-------+ " + color + "[>]");
        Text.sendMessage(sendTo, " ");

        if (page == 0 && !showcaseCommands.isEmpty()) {
            for (ShowcaseCommand s : showcaseCommands) {
                Text.sendMessage(sendTo, "  /" + s.name + " &8-&e " + s.description);
            }
        }

        for (SubCommand s : subCommands.subList(page * COMMANDS_PER_PAGE, Math.min(subCount, (page + 1) * COMMANDS_PER_PAGE))) {
            Text.sendMessage(sendTo, "  " + s.getFormatted(command));
        }
        Text.sendMessage(sendTo, " ");
        Text.sendMessage(sendTo, "  &2<> &f- Required Arguments&7; &9[] &f- Optional Arguments");

        Text.sendMessage(sendTo, color + "[<] &8+-------< " + color + "&l" + description.getName().concat(" &7v" + description.getVersion() + " &8>-------+ " + color + "[>]"));
    }

    public void sendUsage(CommandSender sender) {
        Text.sendMessage(sender, Text.modify("&cUsage: " + getFormatted()));
    }

    public String getFormatted() {
        StringBuilder builder = new StringBuilder().append("/").append(this.command).append(" ");
        for (Argument arg : this.arguments) {
            if (arg.getType() == null) {
                builder.append(arg.getArgument());
            } else {
                if (arg.isOptional())
                    builder.append("[").append(arg.getArgument()).append("]");
                else
                    builder.append("<").append(arg.getArgument()).append(">");
            }
            builder.append(" ");
        }
        return builder.toString();
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
        Argument<S> a = new Argument<>(ArgumentHandler.getArgumentType(clazz), argument, aliases);
        this.arguments.add(a);
        return a;
    }

    protected <S> Argument<S> addArgument(Class<S> clazz, String argument, Function<CommandSender, Collection<String>> onTabComplete, String... aliases) {
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

    public <U> U parseArgument(String[] args, int index) {
        return parseArgument(args, index, null);
    }

    public <U> U parseArgument(String[] args, int index, U def) {
        String arg = args.length - 1 < index ? null : args[index];
        if (arg == null)
            return def;
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


    public Collection<String> tabCompletionSuggestion(CommandSender commandSender, int index) {
        if (index > this.arguments.size() - 1) {
            return Lists.newArrayList();
        }
        return this.arguments.get(index).getOnTabComplete().apply(commandSender);
    }

    private boolean isArgumentValid(String[] arguments, int index) {
        if (getArgumentsSize() - 1 < index) {
            return false;
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

    @AllArgsConstructor
    class ShowcaseCommand {
        private String name;
        private String description;
    }
}