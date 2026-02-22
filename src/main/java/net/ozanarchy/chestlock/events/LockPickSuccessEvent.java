package net.ozanarchy.chestlock.events;

import net.ozanarchy.chestlock.lock.LockInfo;
import net.ozanarchy.chestlock.lock.PickType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LockPickSuccessEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Block block;
    private final LockInfo lockInfo;
    private final PickType pickType;
    private boolean cancelled;

    public LockPickSuccessEvent(Player player, Block block, LockInfo lockInfo, PickType pickType) {
        this.player = player;
        this.block = block;
        this.lockInfo = lockInfo;
        this.pickType = pickType;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    public LockInfo getLockInfo() {
        return lockInfo;
    }

    public PickType getPickType() {
        return pickType;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
