package net.advancedplugins.utils.hooks.plugins;

import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.event.mana.TerraformBlockBreakEvent;
import net.advancedplugins.utils.abilities.DropsSettings;
import net.advancedplugins.utils.abilities.SmeltMaterial;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuraSkillsHook extends PluginHookInstance implements Listener {

    private final ConcurrentHashMap<Vector, BrokenBlockInformation> brokenBlocksMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.AURASKILLS.getPluginName();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLoot(LootDropEvent e) {
        if (e.getCause().equals(LootDropEvent.Cause.EPIC_CATCH))
            return;

        final Player player = e.getPlayer();
        final ItemStack item = e.getItem();
        final Location location = e.getLocation().clone();
        final Vector vector = location.getBlock().getLocation().toVector();

        ItemStack finalItem = item;
        BrokenBlockInformation blockInformation = brokenBlocksMap.get(vector);
        if (!blockInformation.player.equals(player)) return;

        if (blockInformation.settings.isSmelt())
            finalItem = SmeltMaterial.material(item);

        e.setItem(finalItem);
        e.setToInventory(blockInformation.settings.isAddToInventory());
        brokenBlocksMap.remove(vector);
    }

    public boolean isTerraformEvent(BlockBreakEvent event) {
        return event instanceof TerraformBlockBreakEvent;
    }

    public void addBrokenBlockToMap(Block block, Player player, DropsSettings settings) {
        final Location blockLocation = block.getLocation();
        brokenBlocksMap.put(blockLocation.toVector(), new BrokenBlockInformation(player, settings));
        executorService.schedule(() -> brokenBlocksMap.remove(blockLocation.toVector()), 2000, TimeUnit.MILLISECONDS);
    }

    class BrokenBlockInformation {
        public final Player player;
        public final DropsSettings settings;

        public BrokenBlockInformation(Player player, DropsSettings settings) {
            this.player = player;
            this.settings = settings;
        }
    }
}
