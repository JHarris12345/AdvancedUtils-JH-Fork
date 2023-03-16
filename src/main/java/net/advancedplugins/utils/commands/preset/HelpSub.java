package net.advancedplugins.utils.commands.preset;

import net.advancedplugins.utils.commands.SimpleCommand;
import net.advancedplugins.utils.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class HelpSub extends SubCommand<CommandSender> {

    public HelpSub(JavaPlugin plugin, SimpleCommand<?> command) {
        super(plugin, "", true);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {

    }
}
