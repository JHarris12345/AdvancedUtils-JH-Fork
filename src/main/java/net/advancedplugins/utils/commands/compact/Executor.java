package net.advancedplugins.utils.commands.compact;

import net.advancedplugins.utils.commands.SubCommand;
import org.bukkit.command.CommandSender;

public interface Executor {

    void execute(CommandSender sender, String[] args, SubCommand<CommandSender> sub);
}
