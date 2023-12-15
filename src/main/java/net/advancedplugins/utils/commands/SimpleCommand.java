package net.advancedplugins.utils.commands;

import lombok.AllArgsConstructor;
import net.advancedplugins.utils.text.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class SimpleCommand<T extends CommandSender> extends Command<T> {
    private final String command;
    private Integer pageCount;

    private final int COMMANDS_PER_PAGE = 9;

    private LinkedList<SubCommand<? extends CommandSender>> subCommands = new LinkedList<>();
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

    public String getCommand() {
        return this.command;
    }

    public LinkedList<SubCommand<? extends CommandSender>> getSubCommands() {
        return this.subCommands;
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

        int subCount = subCommands.size() + (page == 0 ? showcaseCommands.size() : 0);
        if (pageCount == null) pageCount = (int) Math.ceil((float) (subCount) / COMMANDS_PER_PAGE);
        page = Math.min(Math.max(0, page), pageCount);

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

    @AllArgsConstructor
    class ShowcaseCommand {
        private String name;
        private String description;
    }
}