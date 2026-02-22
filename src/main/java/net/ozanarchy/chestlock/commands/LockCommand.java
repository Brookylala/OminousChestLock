package net.ozanarchy.chestlock.commands;

import net.kyori.adventure.text.Component;
import net.ozanarchy.chestlock.ChestLockPlugin;
import net.ozanarchy.chestlock.lock.LockInfo;
import net.ozanarchy.chestlock.lock.LockService;
import net.ozanarchy.chestlock.lock.PickState;
import net.ozanarchy.chestlock.util.LocationUtil;
import net.ozanarchy.chestlock.util.TimeUtil;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LockCommand implements CommandExecutor {

    private final ChestLockPlugin plugin;
    private final LockService lockService;

    public LockCommand(ChestLockPlugin plugin, LockService lockService) {
        this.plugin = plugin;
        this.lockService = lockService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return true;
        }

        if (!player.hasPermission("chestlock.admin")) { // Original had admin permission for info
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

        String creator = lockInfo.creatorName() == null ? "unknown" : lockInfo.creatorName();
        String lastUser = lockInfo.lastUserName() == null ? "unknown" : lockInfo.lastUserName();
        player.sendMessage(Component.text("Locked with key name: " + lockInfo.keyName()));
        player.sendMessage(Component.text("Created by: " + creator));
        player.sendMessage(Component.text("Last used by: " + lastUser));
        
        PickState state = lockService.getPickState(lockInfo, player); // Using LockService to get PickState
        
        player.sendMessage(Component.text("Rusty pick: " + formatPickStatus(state.rustyAttempts(), state.rustyLimit())));
        player.sendMessage(Component.text("Normal pick: " + formatPickStatus(state.normalAttempts(), state.normalLimit())));
        player.sendMessage(Component.text("Silence pick: " + formatSilenceStatus(state)));
        
        if (lockInfo.lastPickUserName() != null && lockInfo.lastPickType() != null) {
            String when = lockInfo.lastPickTimestamp() > 0L
                    ? TimeUtil.formatDuration(System.currentTimeMillis() - lockInfo.lastPickTimestamp()) + " ago"
                    : "unknown time";
            player.sendMessage(Component.text("Last pick attempt: " + lockInfo.lastPickUserName()
                    + " with " + lockInfo.lastPickType() + " (" + when + ")"));
        }
        return true;
    }

    // Helper methods for formatting pick status, moved from ChestLockPlugin for this command
    private String formatPickStatus(int attempts, int limit) {
        if (limit < 0) {
            return attempts + " attempts (limit pending)";
        }
        boolean lockedOut = attempts >= limit;
        return attempts + "/" + limit + (lockedOut ? " (locked out)" : "");
    }

    private String formatSilenceStatus(PickState state) {
        String base = formatPickStatus(state.silenceAttempts(), state.silenceLimit());
        int overLimit = state.silenceOverLimitAttempts();
        String penalty;
        // The silencePenaltyResetMs is a config value, it should come from ConfigManager via LockService
        // For now, hardcode or pass it. Will pass from LockService.
        long silencePenaltyResetMs = plugin.getConfigManager().getSilencePenaltyResetMs(); // Assuming plugin.getConfigManager() exists
        if (state.silencePenaltyTimestamp() <= 0L) {
            penalty = "penalty ready";
        } else {
            long remaining = silencePenaltyResetMs - (System.currentTimeMillis() - state.silencePenaltyTimestamp());
            penalty = remaining > 0 ? "penalty resets in " + TimeUtil.formatDuration(remaining) : "penalty ready";
        }
        return base + ", over-limit attempts " + overLimit + ", " + penalty;
    }
}