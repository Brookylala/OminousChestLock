package net.ozanarchy.chestlock.commands;

import net.kyori.adventure.text.Component;
import net.ozanarchy.chestlock.ChestLockPlugin;
import net.ozanarchy.chestlock.lock.LockInfo;
import net.ozanarchy.chestlock.lock.LockService;
import net.ozanarchy.chestlock.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AdminLockCommand implements CommandExecutor {

    private final ChestLockPlugin plugin;
    private final LockService lockService;

    public AdminLockCommand(ChestLockPlugin plugin, LockService lockService) {
        this.plugin = plugin;
        this.lockService = lockService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("chestlock.admin")) {
            sender.sendMessage(Component.text("You do not have permission."));
            return true;
        }

        if (args.length < 5) {
            sender.sendMessage(Component.text("Usage: /chestlock lock <world> <x> <y> <z> <player> [keyname]"));
            return true;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            sender.sendMessage(Component.text("Invalid world: " + args[0]));
            return true;
        }

        int x, y, z;
        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid coordinates."));
            return true;
        }

        String playerName = args[4];
        Player creator = Bukkit.getPlayer(playerName);
        if (creator == null) {
            // We can still lock it even if the player is offline if we have their UUID, 
            // but for simplicity in this command let's require them to be online or just use a dummy UUID if needed.
            // Actually tryLock needs a Player object for creator.getName() and creator.getUniqueId().
            sender.sendMessage(Component.text("Player not found: " + playerName));
            return true;
        }

        String keyName = args.length >= 6 ? args[5] : "Town_" + playerName + "_" + System.currentTimeMillis();

        Block block = world.getBlockAt(x, y, z);
        if (!LocationUtil.isLockable(block)) {
            sender.sendMessage(Component.text("Block at location is not lockable."));
            return true;
        }

        if (lockService.getLockInfo(block) != null) {
            sender.sendMessage(Component.text("Block is already locked."));
            return true;
        }

        boolean success = lockService.tryLock(block, keyName, creator, false);
        if (success) {
            sender.sendMessage(Component.text("Successfully locked block at " + x + ", " + y + ", " + z + " for " + playerName + " with key " + keyName));
            lockService.logLockEvent("ADMIN_LOCK", sender.getName(), keyName, block.getLocation(), null, "locked via admin command for " + playerName);
        } else {
            sender.sendMessage(Component.text("Failed to lock block. Maybe key name is already in use?"));
        }

        return true;
    }
}
