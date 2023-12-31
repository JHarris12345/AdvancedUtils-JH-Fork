package net.advancedplugins.utils.protection.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class FakeAdvancedBlockBreakEvent extends BlockBreakEvent {
    private final Player player;
    private boolean dropItems;
    private boolean cancel;

    public FakeAdvancedBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player) {
        super(theBlock, player);
        this.player = player;
        this.dropItems = true;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public boolean isDropItems() {
        return this.dropItems;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}