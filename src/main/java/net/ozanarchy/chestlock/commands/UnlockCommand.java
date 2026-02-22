package net.ozanarchy.chestlock.commands;

import net.kyori.adventure.text.Component;
import net.ozanarchy.chestlock.ChestLockPlugin;
import net.ozanarchy.chestlock.lock.LockInfo;
import net.ozanarchy.chestlock.lock.LockService;
import net.ozanarchy.chestlock.util.LocationUtil;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnlockCommand implements CommandExecutor {

    private final ChestLockPlugin plugin;
    private final LockService lockService;

    public UnlockCommand(ChestLockPlugin plugin, LockService lockService) {
        this.plugin = plugin;
        this.lockService = lockService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return true;
        }

        if (!player.hasPermission("chestlock.admin")) {
            player.sendMessage(Component.text("You do not have permission."));
            return true;
        }

        Block target = player.getTargetBlockExact(5);
        if (target == null || !LocationUtil.isLockable(target)) {
            player.sendMessage(Component.text("Look at a chest, barrel, or shulker within 5 blocks."));
            return true;
        }

        LockInfo lockInfo = lockService.getLockInfo(target);
        if (lockInfo == null) {
            player.sendMessage(Component.text("That container is not locked."));
            return true;
        }

        lockService.unlock(target, lockInfo.keyName());
        player.sendMessage(Component.text("Unlocked container (key name was: " + lockInfo.keyName() + ")."));
        lockService.logLockEvent("UNLOCK_CMD", player.getName(), lockInfo.keyName(), target.getLocation(), null, "unlocked via command");
        return true;
    }
}
