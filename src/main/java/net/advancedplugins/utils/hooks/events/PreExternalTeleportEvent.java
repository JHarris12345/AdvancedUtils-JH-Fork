package net.advancedplugins.utils.hooks.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class PreExternalTeleportEvent extends Event implements Cancellable {
    private boolean cancelled;
    private Player player;
    private Location location;

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }

    public static @NotNull HandlerList getHandlerList() {
        return new HandlerList();
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }


}
