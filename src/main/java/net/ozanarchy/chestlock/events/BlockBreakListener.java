package net.ozanarchy.chestlock.events;

import net.kyori.adventure.text.Component;
import net.ozanarchy.chestlock.ChestLockPlugin;
import net.ozanarchy.chestlock.lock.LockInfo;
import net.ozanarchy.chestlock.lock.LockService;
import net.ozanarchy.chestlock.model.KeyMatch;
import net.ozanarchy.chestlock.util.LocationUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;

public class BlockBreakListener implements Listener {

    private final ChestLockPlugin plugin;
    private final LockService lockService;

    public BlockBreakListener(ChestLockPlugin plugin, LockService lockService) {
        this.plugin = plugin;
        this.lockService = lockService;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        // Check if it's a hopper and remove its owner
        if (block.getType() == Material.HOPPER) {
            Map<String, net.ozanarchy.chestlock.model.HopperOwner> hopperOwners = lockService.getHopperOwners();
            hopperOwners.remove(LocationUtil.locationKey(block.getLocation()));
            // No need to saveData here, it will be saved on plugin disable or explicit dataStore.saveData() call
        }

        if (!LocationUtil.isLockable(block)) {
            return;
        }

        LockInfo lockInfo = lockService.getLockInfo(block);
        if (lockInfo == null) {
            return;
        }

        KeyMatch keyMatch = lockService.findHeldKey(event.getPlayer(), lockInfo.keyName());
        String heldKeyName = keyMatch == null ? null : keyMatch.name();
        if (heldKeyName == null || !lockInfo.keyName().equals(heldKeyName)) {
            event.setCancelled(true);
            lockService.playFail(event.getPlayer(), block.getLocation());
            lockService.logLockEvent("BREAK_DENY", event.getPlayer().getName(), heldKeyName, block.getLocation(), lockInfo, null);
            return;
        }

        lockService.unlock(block, lockInfo.keyName());
        lockService.logLockEvent("BREAK_ALLOWED", event.getPlayer().getName(), heldKeyName, block.getLocation(), lockInfo, null);
    }
}
