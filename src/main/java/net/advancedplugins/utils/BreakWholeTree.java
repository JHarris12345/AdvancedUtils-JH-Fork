package net.advancedplugins.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;

public class BreakWholeTree {

    private final int maxLeavesToSearch = 90;
    private final int maxLogsToSearch = 95; // may need further tinkering
    private int leavesScanned = 0;

    private final Set<Block> foundBlocks;

    public BreakWholeTree(final Block block) {
        Location originLocation = block.getLocation();
        this.foundBlocks = new HashSet<>();
        //Material originType = block.getType();
        final World world = originLocation.getWorld();

        int maxTreeRadius = 2;
        final int stopX = originLocation.getBlockX() + maxTreeRadius;
        final int stopZ = originLocation.getBlockZ() + maxTreeRadius;

        for (int x = originLocation.getBlockX(); x <= stopX; x++) {
            for (int z = originLocation.getBlockZ(); z <= stopZ; z++) {
                if (this.foundBlocks.size() >= maxLogsToSearch || leavesScanned >= maxLeavesToSearch) {
                    return;
                }

                final Location toScan = new Location(world, x, originLocation.getY(), z);
                final Block locBlock = toScan.getBlock();
                if (!ASManager.isLog(locBlock.getType())) {
                    if (locBlock.getType().name().endsWith("LEAVES")) {
                        leavesScanned++;
                        continue;
                    } else if (locBlock.getType().name().contains("AIR")) {
                        continue;
                    }
                }

                if (locBlock.hasMetadata("AE_Placed")) continue;
                // don't break player placed blocks
                if (locBlock.hasMetadata("non-natural") /*&& YamlFile.CONFIG.getBoolean("settings.respect-player-placed-blocks", true)*/) {
                    continue;
                }


                for (final BlockFace face : BlockFace.values()) {
                    final Block toSearch = locBlock.getRelative(face);
                    if (!ASManager.isLog(toSearch.getType()) || toSearch.getType().name().endsWith("LEAVES")) continue;
                    this.foundBlocks.add(toSearch);
                    switch (face) {

                        case UP: {
                            this.scanUp(toSearch);
                            break;
                        }

                        case DOWN: {
                            this.scanDown(toSearch);
                            break;
                        }

                        case NORTH_WEST:
                        case NORTH_EAST:
                        case SOUTH_WEST:
                        case SOUTH_EAST:
                        case NORTH:
                        case WEST:
                        case SOUTH:
                        case EAST: {
                            this.scanDirectional(toSearch, face);
                            break;
                        }
                        default: {
                            break;
                        }
                    }

                }

            }
        }
        foundBlocks.remove(block); // Remove the main block to avoid breaking it twice. (https://github.com/GC-spigot/AdvancedEnchantments/issues/3269)
    }

    private void scanUp(final Block start) {
        if (this.foundBlocks.size() >= maxLogsToSearch || leavesScanned >= maxLeavesToSearch) {
            return;
        }
        if (start.hasMetadata("AE_Placed")) return;
        // don't break player placed blocks
        if (start.hasMetadata("non-natural") /*&& YamlFile.CONFIG.getBoolean("settings.respect-player-placed-blocks", true)*/) {
            return;
        }
        if (!ASManager.isLog(start.getType())) {
            if (start.getType().name().endsWith("LEAVES")) {
                leavesScanned++;
                scanUp(start.getRelative(BlockFace.UP));
            }
        } else {
            this.foundBlocks.add(start);
            scanUp(start.getRelative(BlockFace.UP));
        }
    }

    private void scanDown(final Block start) {
        if (this.foundBlocks.size() >= maxLogsToSearch || leavesScanned >= maxLeavesToSearch) {
            return;
        }
        if (start.hasMetadata("AE_Placed")) return;
        if (start.hasMetadata("non-natural")/* && YamlFile.CONFIG.getBoolean("settings.respect-player-placed-blocks", true)*/) {
            return;
        }
        if (!ASManager.isLog(start.getType())) {
            if (start.getType().name().endsWith("LEAVES")) {
                leavesScanned++;
                scanDown(start.getRelative(BlockFace.DOWN));
            }
        } else {
            this.foundBlocks.add(start);
            scanUp(start.getRelative(BlockFace.DOWN));
        }
    }

    private void scanDirectional(Block start, final BlockFace direction) {
        this.scanUp(start);
        this.scanDown(start);
    }

    public Set<Block> get() {
        return this.foundBlocks;
    }

}
