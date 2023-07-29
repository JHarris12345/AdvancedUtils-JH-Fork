package net.advancedplugins.utils.hooks.plugins;

import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.api.event.TerraformBlockBreakEvent;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.abilities.DropsSettings;
import net.advancedplugins.utils.abilities.SmeltMaterial;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.PluginHookInstance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AureliumSkillsHook extends PluginHookInstance implements Listener {

    private ConcurrentHashMap<Vector, BrokenBlockInformation> brokenBlocksMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.AURELIUMSKILLS.getPluginName();
    }

    @EventHandler
    public void onLoot(PlayerLootDropEvent e) {
        Vector vector = e.getLocation().getBlock().getLocation().toVector();
        if (!brokenBlocksMap.containsKey(vector)) {
            return;
        }

        BrokenBlockInformation blockInformation = brokenBlocksMap.get(vector);
        ItemStack item = e.getItemStack();

        if (blockInformation.settings.isSmelt()) {
            item = SmeltMaterial.material(item, true);
            e.setItemStack(item);
        }

        if (blockInformation.settings.isAddToInventory()) {
            e.setCancelled(true);
            e.setLocation(e.getLocation().add(0, 10, 0));
            e.setItemStack(new ItemStack(Material.AIR));
            ASManager.giveItem(blockInformation.player, item);
        }
    }

    public boolean isTerraformEvent(BlockBreakEvent event) {
        return event instanceof TerraformBlockBreakEvent;
    }

    public void addBrokenBlockToMap(Block block, Player player, DropsSettings settings) {
        final Location blockLocation = block.getLocation();
        brokenBlocksMap.put(blockLocation.toVector(), new BrokenBlockInformation(player, settings));
        executorService.schedule(() -> brokenBlocksMap.remove(blockLocation.toVector()), 200, TimeUnit.MILLISECONDS);
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
