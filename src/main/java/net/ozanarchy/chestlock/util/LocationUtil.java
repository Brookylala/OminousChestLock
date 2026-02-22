package net.ozanarchy.chestlock.util;

import net.ozanarchy.chestlock.model.LocationData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.block.DoubleChest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocationUtil {

    public static String locationKey(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }
        return location.getWorld().getName() + ":" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static LocationData parseLocationKey(String locationKey) {
        if (locationKey == null || locationKey.isBlank()) {
            return null;
        }
        String[] parts = locationKey.split(":");
        if (parts.length != 2) {
            return null;
        }
        String worldName = parts[0];
        String[] coords = parts[1].split(",");
        if (coords.length != 3) {
            return null;
        }
        try {
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int z = Integer.parseInt(coords[2]);
            return new LocationData(worldName, (UUID) null, (String) null, x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isLockable(Block block) {
        return block != null && (block.getType() == org.bukkit.Material.LODESTONE
                || block.getState() instanceof Chest
                || block.getState() instanceof Barrel
                || block.getState() instanceof ShulkerBox);
    }

    public static List<Location> resolveLockLocations(Block block) {
        if (block == null) {
            return List.of();
        }
        if (block.getType() == org.bukkit.Material.LODESTONE) {
            return List.of(block.getLocation());
        }
        if (block.getState() instanceof InventoryHolder holder) {
            return resolveLockLocations(holder);
        }
        return List.of();
    }

    public static List<Location> resolveLockLocations(InventoryHolder holder) {
        List<Location> locations = new ArrayList<>();
        if (holder instanceof DoubleChest doubleChest) {
            if (doubleChest.getLeftSide() instanceof Chest left) {
                locations.add(left.getLocation());
            }
            if (doubleChest.getRightSide() instanceof Chest right) {
                locations.add(right.getLocation());
            }
        } else if (holder instanceof Chest chest) {
            InventoryHolder chestHolder = chest.getInventory().getHolder();
            if (chestHolder instanceof DoubleChest doubleChest) {
                if (doubleChest.getLeftSide() instanceof Chest left) {
                    locations.add(left.getLocation());
                }
                if (doubleChest.getRightSide() instanceof Chest right) {
                    locations.add(right.getLocation());
                }
            } else {
                locations.add(chest.getLocation());
            }
        } else if (holder instanceof Barrel barrel) {
            locations.add(barrel.getLocation());
        } else if (holder instanceof ShulkerBox shulkerBox) {
            locations.add(shulkerBox.getLocation());
        }
        locations.removeIf(location -> location == null || location.getWorld() == null);
        return locations;
    }

    public static String formatLocation(Location location) {
        if (location == null) {
            return "unknown";
        }
        World world = location.getWorld();
        String worldName = world == null ? "unknown" : world.getName();
        String realm = world == null ? "unknown" : mapRealm(world.getEnvironment()); // mapRealm is missing, will need to be added or handled
        return worldName + ":" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ()
                + " (" + realm + ")";
    }

    // This method was present in ChestLockPlugin, it needs to be moved or re-implemented
    private static String mapRealm(World.Environment environment) {
        return switch (environment) {
            case NORMAL -> "overworld";
            case NETHER -> "nether";
            case THE_END -> "the_end";
            default -> "unknown";
        };
    }
}
