package net.advancedplugins.utils.hooks.plugins;

import github.scarsz.discordsrv.DiscordSRV;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;


public class DiscordSRVHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.DISCORDSRV.getPluginName();
    }

    public void processChatMessage(@NotNull Player p, @NotNull String msg, @NotNull String channel, @NotNull Event event) {
        DiscordSRV.getPlugin().processChatMessage(p, msg, channel, false, event);
    }

    public void processGlobalChatMessage(@NotNull Player p, @NotNull String msg, @NotNull Event event) {
        this.processChatMessage(p, msg, DiscordSRV.getPlugin().getOptionalChannel("global"), event);
    }
}
