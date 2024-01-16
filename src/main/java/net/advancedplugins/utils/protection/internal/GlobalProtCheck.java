package net.advancedplugins.utils.protection.internal;

import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.protection.ProtectionType;
import net.advancedplugins.utils.protection.events.FakeAdvancedBlockBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class GlobalProtCheck implements ProtectionType, Listener {

    public GlobalProtCheck() {
        Bukkit.getPluginManager().registerEvents(this, ASManager.getInstance());
    }

    @Override
    public String getName() {
        return "vanilla";
    }

    @Override
    public boolean canAttack(Player p, Player p2) {
        return true;
    }

    @Override
    public boolean canBreak(Player p, Location loc) {
        Block block = loc.getBlock();

        block.setMetadata("blockbreakevent-ignore", new FixedMetadataValue(ASManager.getInstance(), true));

        BlockBreakEvent event = new FakeAdvancedBlockBreakEvent(block, p);
//        if (HooksHandler.isEnabled(HookPlugin.MCMMO)) {
//            try {
//                // If we don't use reflection for this, it'll throw a NoClassDefFoundError on the line that calls this method.
//                Constructor<com.gmail.nossr50.events.fake.FakeBlockBreakEvent> constructor = com.gmail.nossr50.events.fake.FakeBlockBreakEvent.class.getConstructor(Block.class, Player.class);
//                constructor.setAccessible(true);
//                event = constructor.newInstance(block, p);
//            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ignored) {
//            }
//        }

        Bukkit.getPluginManager().callEvent(event);

        // if event was cancelled by protection plugin, and not us
        boolean cancelled = event.isCancelled() && !block.hasMetadata("ae-fake-cancel");

        if (block.hasMetadata("blockbreakevent-ignore")) {
            block.removeMetadata("blockbreakevent-ignore", ASManager.getInstance());
        }

        if (block.hasMetadata("ae-fake-cancel")) {
            block.removeMetadata("ae-fake-cancel", ASManager.getInstance());
        }

        return !cancelled;
    }

    @Override
    public boolean isProtected(Location loc) {
        return false;
    }

    /*
        This should leave enough room for every protection plugin to cancel the plugin.
        If no one cancels it before this, we cancel it ourselves to prevent the event being handled
        by other plugins, which might give xp for block break etc...
         */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onFakeBlockBreak(FakeAdvancedBlockBreakEvent fakeEvent) {
        fakeEvent.setCancelled(true);
        fakeEvent.getBlock().setMetadata("ae-fake-cancel", new FixedMetadataValue(ASManager.getInstance(), true));
    }
}