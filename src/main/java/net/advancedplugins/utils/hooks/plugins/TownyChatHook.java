package net.advancedplugins.utils.hooks.plugins;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.entity.Player;

public class TownyChatHook extends PluginHookInstance {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.TOWNYCHAT.getPluginName();
    }

    public boolean isInTownyChannel(Player p) {
        Channel channel = Chat.getTownyChat().getPlayerChannel(p);
        return channel != null && channel.getType() != channelTypes.GLOBAL;
    }
}
