package net.advancedplugins.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/***
 * "Borrowed" from decompiled
 * <a href="https://www.spigotmc.org/resources/treefix.17267/">TreeFix</a>
 */
public class BreakWholeTree {
    private int leavesScanned = 0;

    private final Set<Block> foundBlocks;
    private final List<Location> logs = new ArrayList<>();
    private final List<Location> leaves = new ArrayList<>();
    private final int maxLogs;
    private final int maxLeaves;

    private final List<BlockFace> dirs = Arrays.asList(
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST
    );

    public BreakWholeTree(final Block block, int maxLogs, int maxLeaves) {
        this.foundBlocks = new HashSet<>();
        this.maxLogs = maxLogs > 0 ? maxLogs : 1000;
        this.maxLeaves = maxLeaves > 0 ? maxLeaves : 300;
        Location lowestBlock = block.getLocation();

        findLog(block, true, true);
        findLog(block, false, true);

        for (Location log : this.logs) {
            if (log.getY() < lowestBlock.getY()) {
                lowestBlock = log;
            }
        }

        foundBlocks.addAll(logs.stream().map(Location::getBlock).collect(Collectors.toSet()));
        foundBlocks.remove(block); // Remove the main block to avoid breaking it twice.
    }

    private boolean findLog(Block block, boolean up, boolean prevFound) {
        if (foundBlocks.size() >= maxLogs || leavesScanned >= maxLeaves) {
            return false;
        }

        boolean found = false;
        if (ASManager.isLog(block.getType()) && !logs.contains(block.getLocation())) {
            logs.add(block.getLocation());
            found = true;
        }

        if (block.getType().name().endsWith("LEAVES") && !leaves.contains(block.getLocation())) {
            leaves.add(block.getLocation());
            leavesScanned++;
        }

        if (found) {
            for (BlockFace dir : dirs) {
                findLog(block.getRelative(dir), up, true);
            }
        }

        if (found || prevFound) {
            findLog(block.getRelative(up ? BlockFace.UP : BlockFace.DOWN), up, found);
        }

        return found;
    }

    public Set<Block> get() {
        return this.foundBlocks;
    }
}