package net.advancedplugins.utils.hooks.plugins;

//import com.Zrips.CMI.events.CMIPlayerTeleportEvent;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import net.advancedplugins.utils.hooks.VanishHook;
import net.advancedplugins.utils.hooks.events.PreExternalTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CMIHook extends PluginHookInstance implements VanishHook, Listener {

    public CMIHook() {
        Bukkit.getPluginManager().registerEvents(this, ASManager.getInstance());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.CMI.getPluginName();
    }

    public boolean isPlayerVanished(Player player) {
        return com.Zrips.CMI.CMI.getInstance().getPlayerManager().getUser(player).isVanished();
    }

//    @EventHandler
//    public void onTeleport(CMIPlayerTeleportEvent e) {
//        PreExternalTeleportEvent event = new PreExternalTeleportEvent(false, e.getPlayer(), e.getTo());
//        Bukkit.getPluginManager().callEvent(event);
//
//        if(event.isCancelled()) {
//            e.setCancelled(true);
//        }
//    }

}
