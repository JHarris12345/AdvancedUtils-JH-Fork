package net.advancedplugins.utils.commands;

import lombok.Getter;
import net.advancedplugins.utils.commands.argument.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


@Getter
public abstract class ConfigSubCommand<T extends CommandSender> extends ConfigCommand<T> {
    private final boolean endless;

    public ConfigSubCommand(JavaPlugin plugin, Config config, boolean console, boolean endless) {
        super(plugin, config, console);
        this.endless = endless;
    }

    public String getFormatted(String command) {
        StringBuilder builder = new StringBuilder().append("/").append(command).append(" ");
        for (Argument<?> arg : this.getArguments()) {
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
        builder.append(this.getConfig().getDescription());
        return builder.toString();
    }


    @Override
    public boolean isArgumentValid(String[] arguments, int index) {
        if (getArgumentsSizeReal() - 1 < index)
            return this.endless;

        return super.isArgumentValid(arguments, index);
    }
}