package net.ozanarchy.chestlock.events;

import net.ozanarchy.chestlock.ChestLockPlugin;
import net.ozanarchy.chestlock.lock.LockService;
import net.ozanarchy.chestlock.model.HopperOwner;
import net.ozanarchy.chestlock.util.ItemUtil;
import net.ozanarchy.chestlock.util.LocationUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Map;

public class HopperListener implements Listener {

    private final ChestLockPlugin plugin;
    private final LockService lockService;

    public HopperListener(ChestLockPlugin plugin, LockService lockService) {
        this.plugin = plugin;
        this.lockService = lockService;
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        InventoryHolder sourceHolder = event.getSource().getHolder();
        InventoryHolder destinationHolder = event.getDestination().getHolder();
        if (lockService.isLockedHolder(sourceHolder) || lockService.isLockedHolder(destinationHolder)) {
            event.setCancelled(true);
            String detail = "source=" + event.getSource().getType() + " dest=" + event.getDestination().getType();
            lockService.logInventoryMove(event, detail);
        }
    }

    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        if (lockService.isLockedHolder(event.getInventory().getHolder())) {
            event.setCancelled(true);
            InventoryHolder holder = event.getInventory().getHolder();
            lockService.logLockEvent("INVENTORY_PICKUP_DENY", lockService.inventoryActor(holder),
                    null, lockService.lockLocation(event.getInventory()), lockService.getLockInfo(lockService.lockLocation(event.getInventory())), "inventory=" + event.getInventory().getType());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.HOPPER) {
            Map<String, HopperOwner> hopperOwners = lockService.getHopperOwners();
            hopperOwners.put(LocationUtil.locationKey(event.getBlockPlaced().getLocation()),
                    new HopperOwner(event.getPlayer().getName(), System.currentTimeMillis()));
        }
        if (!ItemUtil.isDecorationItem(event.getItemInHand())) {
            return;
        }
        Block against = event.getBlockAgainst();
        if (against == null || !LocationUtil.isLockable(against)) {
            return;
        }
        if (lockService.getLockInfo(against) == null) { // Only allow placing decoration on unlocked or already locked chests
            return;
        }
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        switch (event.getEntity().getType()) {
            case ITEM_FRAME, GLOW_ITEM_FRAME -> {
                Block against = event.getBlock();
                if (against == null || !LocationUtil.isLockable(against)) {
                    return;
                }
                if (lockService.getLockInfo(against) == null) { // Only allow placing decoration on unlocked or already locked chests
                    return;
                }
                event.setCancelled(false);
            }
            default -> {
            }
        }
    }
}
