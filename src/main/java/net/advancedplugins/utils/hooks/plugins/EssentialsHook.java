package net.advancedplugins.utils.hooks.plugins;

import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import net.advancedplugins.utils.hooks.VanishHook;
//import net.advancedplugins.utils.hooks.events.PreExternalTeleportEvent;
//import net.ess3.api.events.teleport.PreTeleportEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsHook extends PluginHookInstance implements VanishHook, Listener {

    public EssentialsHook() {
        Bukkit.getPluginManager().registerEvents(this, ASManager.getInstance());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.ESSENTIALS.getPluginName();
    }

    public boolean isPlayerVanished(Player player) {
        return ((com.earth2me.essentials.Essentials) getPluginInstance()).getUser(player).isVanished();
    }

//    @EventHandler
//    public void onPreTeleport(PreTeleportEvent event) {
//        PreExternalTeleportEvent preEvent = new PreExternalTeleportEvent(false, event.getTeleporter().getBase(), event.getTarget().getLocation());
//        Bukkit.getPluginManager().callEvent(preEvent);
//        if (preEvent.isCancelled()) {
//            event.setCancelled(true);
//        }
//    }

}
