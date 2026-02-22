package net.ozanarchy.chestlock.events;

import net.ozanarchy.chestlock.ChestLockPlugin;
import net.ozanarchy.chestlock.lock.LockService;
import net.ozanarchy.chestlock.model.PendingIgnite;
import net.ozanarchy.chestlock.util.LocationUtil;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.block.BlockFace;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;
import java.util.UUID;

public class BlockProtectionListener implements Listener {

    private final ChestLockPlugin plugin;
    private final LockService lockService;

    public BlockProtectionListener(ChestLockPlugin plugin, LockService lockService) {
        this.plugin = plugin;
        this.lockService = lockService;
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Block ignited = event.getBlock();
        long now = System.currentTimeMillis();
        Map<String, PendingIgnite> tntIgnites = lockService.getTntIgnites();
        if (ignited.getType().name().equals("TNT")) { // Use type.name() to avoid direct Material import for specific block types if not strictly necessary
            tntIgnites.put(LocationUtil.locationKey(ignited.getLocation()), new PendingIgnite(player.getName(), now));
            return;
        }
        for (BlockFace face : new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
            Block adjacent = ignited.getRelative(face);
            if (adjacent.getType().name().equals("TNT")) {
                tntIgnites.put(LocationUtil.locationKey(adjacent.getLocation()), new PendingIgnite(player.getName(), now));
            }
        }
    }

    @EventHandler
    public void onTntSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed tnt)) {
            return;
        }
        String key = LocationUtil.locationKey(tnt.getLocation());
        Map<String, PendingIgnite> tntIgnites = lockService.getTntIgnites();
        Map<UUID, String> tntSources = lockService.getTntSources();
        PendingIgnite ignite = tntIgnites.get(key);
        if (ignite != null && System.currentTimeMillis() - ignite.timestamp() < 10000L) {
            tntSources.put(tnt.getUniqueId(), ignite.playerName());
        }
    }

    @EventHandler
    public void onCrystalDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal crystal)) {
            return;
        }
        String playerName = null;
        if (event.getDamager() instanceof Player player) {
            playerName = player.getName();
        } else if (event.getDamager() instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player player) {
                playerName = player.getName();
            }
        }
        if (playerName != null) {
            Map<UUID, PendingIgnite> crystalSources = lockService.getCrystalSources();
            crystalSources.put(crystal.getUniqueId(), new PendingIgnite(playerName, System.currentTimeMillis()));
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> {
            if (lockService.isLockedBlock(block)) {
                lockService.logLockEvent("EXPLOSION_DENY", "BLOCK_EXPLODE:" + event.getBlock().getType(), null, block.getLocation(), lockService.getLockInfo(block), null);
                return true;
            }
            return false;
        });
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            if (lockService.isLockedBlock(block)) {
                String source = lockService.explosionActor(event.getEntity());
                lockService.logLockEvent("EXPLOSION_DENY", source, null, block.getLocation(), lockService.getLockInfo(block), null);
                return true;
            }
            return false;
        });
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (lockService.isLockedBlock(event.getBlock())) {
            event.setCancelled(true);
            lockService.logLockEvent("BURN_DENY", "FIRE", null, event.getBlock().getLocation(), lockService.getLockInfo(event.getBlock()), null);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (lockService.isLockedBlock(block)) {
                event.setCancelled(true);
                lockService.logLockEvent("PISTON_EXTEND_DENY", "PISTON", null, block.getLocation(), lockService.getLockInfo(block), null);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (lockService.isLockedBlock(block)) {
                event.setCancelled(true);
                lockService.logLockEvent("PISTON_RETRACT_DENY", "PISTON", null, block.getLocation(), lockService.getLockInfo(block), null);
                return;
            }
        }
    }
}
