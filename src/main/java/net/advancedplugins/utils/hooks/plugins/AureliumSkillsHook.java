package net.advancedplugins.utils.hooks.plugins;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.api.event.PlayerLootDropEvent;
import com.archyx.aureliumskills.api.event.TerraformBlockBreakEvent;
import com.archyx.aureliumskills.skills.mining.MiningLootHandler;
import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.LocalLocation;
import net.advancedplugins.utils.SchedulerUtils;
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

public class AureliumSkillsHook extends PluginHookInstance implements Listener {

    private final ConcurrentHashMap<Vector, BrokenBlockInformation> brokenBlocksMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return HookPlugin.AURELIUMSKILLS.getPluginName();
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoot(PlayerLootDropEvent e) {
        if(e.getCause().equals(LootDropCause.EPIC_CATCH))
            return;
        if (e.isCancelled())
            return;

        if (e.getCause().name().contains("CATCH")) {
            return;
        }

        final Player player = e.getPlayer();
        final ItemStack item = e.getItemStack();
        final Location location = e.getLocation().clone();

        /*
         if item is set to AIR or null, AureliumSkills will throw either NullException or that AIR cannot be dropped,
         if the event is cancelled, the ItemStack shouldn't be set however
         https://github.com/Archy-X/AureliumSkills/blob/master/bukkit/src/main/java/com/archyx/aureliumskills/skills/fishing/FishingAbilities.java#L48
        */
        // e.setItemStack(new ItemStack(Material.AIR));
        e.setCancelled(true);
        e.setLocation(e.getLocation().add(0, 10, 0));

        ASManager.debug("[aureliumskills extra loot] Dropped " + item.getType().name() + " for " + player.getName() + " at " + new LocalLocation(location).getEncode());
        executorService.schedule(() -> {
            Vector vector = location.getBlock().getLocation().toVector();
            if (!brokenBlocksMap.containsKey(vector)) {
                SchedulerUtils.runTaskLater(() -> location.getWorld().dropItem(location, item));
                return;
            }

            ItemStack finalItem = item;
            BrokenBlockInformation blockInformation = brokenBlocksMap.get(vector);

            if (blockInformation.settings.isSmelt()) {
                finalItem = SmeltMaterial.material(item, true);
            }

            if (blockInformation.settings.isAddToInventory()) {
                ASManager.giveItem(blockInformation.player, finalItem);
            }
        }, 20, TimeUnit.MILLISECONDS);

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

    class CustomLootHandler extends MiningLootHandler {
        public CustomLootHandler(AureliumSkills plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.LOW)
        @Override
        public void onBreak(BlockBreakEvent event) {
            super.onBreak(event);
        }
    }
}
