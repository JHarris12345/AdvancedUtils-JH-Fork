package net.advancedplugins.utils.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.advancedplugins.utils.DataHandler;
import net.advancedplugins.utils.commands.argument.Argument;
import net.advancedplugins.utils.commands.argument.ArgumentHandler;
import net.advancedplugins.utils.text.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public abstract class ConfigCommand<T extends CommandSender> {
    private static final int COMMANDS_PER_PAGE = 9;
    private Integer pageCount;
    private LinkedList<ConfigSubCommand<? extends CommandSender>> subCommands = new LinkedList<>();
    @Setter
    private List<Argument<?>> arguments = new ArrayList<>();
    private final LinkedList<ShowcaseCommand> showcaseCommands = new LinkedList<>();

    private final Config config;
    protected final JavaPlugin plugin;
    private final boolean console;
    @Setter
    private Function<T, String> noPermission = player -> "&cYou do not have permission to do this.";
    @Setter
    private Function<T, String> notOnline = player -> "&cPlayer is not online.";


    public ConfigCommand(JavaPlugin plugin, Config config, boolean console) {
        this.config = config;
        this.plugin = plugin;
        this.console = console;
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

    public abstract void onExecute(T sender, String[] args);

    @SuppressWarnings("unchecked")
    public void middleMan(CommandSender sender, String[] args) {
        this.onExecute((T) sender, args);
    }

    public void addShowcaseCommand(String name, String description) {
        showcaseCommands.add(new ShowcaseCommand(name, description));
    }

    protected void setSubCommands(ConfigSubCommand<? extends CommandSender>... subCommands) {
        this.subCommands = new LinkedList<>(Arrays.asList(subCommands));
    }

    public void sendHelpMessage(Plugin plugin, CommandSender sendTo) {
        PluginDescriptionFile description = plugin.getDescription();
        Text.sendMessage(sendTo, "&f".concat(description.getName()).concat(" &7v").concat(description.getVersion()));
        Text.sendMessage(sendTo, "&7Use &f&n".concat(this.config.getCommand()).concat(" to view usage information."));
    }

    public void sendHelpPage(CommandSender sendTo, String color, String[] args) {
        int page = (args.length == 0 ? 0 : StringUtils.isNumeric(args[0]) ? Math.max(0, Integer.parseInt(args[0])) : 1) - 1;

        int subCount = subCommands.size();
        if (pageCount == null) pageCount = (int) Math.ceil((float) (subCount) / COMMANDS_PER_PAGE);
        page = Math.min(Math.max(0, page), pageCount);

        if (page + 1 > pageCount) {
            page = pageCount - 1;
        }

        PluginDescriptionFile description = this.plugin.getDescription();
        Text.sendMessage(sendTo, color + "[<] &8+-------< " + color + "&l" + description.getName().concat(" &7Page " + (page + 1) + "/" + pageCount) + " &8>-------+ " + color + "[>]");
        Text.sendMessage(sendTo, " ");

        if (page == 0 && !showcaseCommands.isEmpty()) {
            for (ShowcaseCommand s : showcaseCommands) {
                Text.sendMessage(sendTo, "  /" + s.name + " &8-&e " + s.description);
            }
        }

        for (ConfigSubCommand<? extends CommandSender> s : subCommands.subList(page * COMMANDS_PER_PAGE, Math.min(subCount, (page + 1) * COMMANDS_PER_PAGE))) {
            Text.sendMessage(sendTo, "  " + s.getFormatted(this.getConfig().getCommand()));
        }
        Text.sendMessage(sendTo, " ");
        Text.sendMessage(sendTo, "  &2<> &f- Required Arguments&7; &9[] &f- Optional Arguments");

        Text.sendMessage(sendTo, color + "[<] &8+-------< " + color + "&l" + description.getName().concat(" &7v" + description.getVersion() + " &8>-------+ " + color + "[>]"));
    }

    public void sendUsage(CommandSender sender) {
        Text.sendMessage(sender, Text.modify("&cUsage: " + getFormatted()));
    }

    public String getFormatted() {
        StringBuilder builder = new StringBuilder().append("/").append(this.getConfig().getCommand()).append(" ");
        for (Argument<?> arg : this.arguments) {
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

    public boolean isArgumentValid(String[] arguments, int index) {
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
    static class ShowcaseCommand {
        private String name;
        private String description;
    }

    public boolean isPlayerOnline(CommandSender sender, Player p) {
        if (p == null || !p.isOnline()) {
            sender.sendMessage(this.notOnline.apply((T) sender));
            return false;
        }
        return true;
    }

    public String getNoPermissionLang(CommandSender sender) {
        return this.noPermission.apply((T) sender);
    }

    @SuppressWarnings("unchecked")
    public String getNotOnlineLang(CommandSender sender) {
        return this.notOnline.apply((T) sender);
    }
}
