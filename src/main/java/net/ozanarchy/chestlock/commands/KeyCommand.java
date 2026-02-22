package net.ozanarchy.chestlock.commands;

import net.kyori.adventure.text.Component;
import net.ozanarchy.chestlock.ChestLockPlugin;
import net.ozanarchy.chestlock.lock.LockInfo;
import net.ozanarchy.chestlock.lock.LockService;
import net.ozanarchy.chestlock.model.KeyMatch;
import net.ozanarchy.chestlock.model.LocationData;
import net.ozanarchy.chestlock.util.LocationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KeyCommand implements CommandExecutor {

    private final ChestLockPlugin plugin;
    private final LockService lockService;

    public KeyCommand(ChestLockPlugin plugin, LockService lockService) {
        this.plugin = plugin;
        this.lockService = lockService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players."));
            return true;
        }

        if (!player.hasPermission("chestlock.admin")) { // Original had admin permission for keyinfo
            player.sendMessage(Component.text("You do not have permission."));
            return true;
        }

        KeyMatch heldKey = lockService.findAnyHeldKey(player); // Use LockService to find held key
        String keyName = heldKey == null ? null : heldKey.name();
        if (keyName == null) {
            player.sendMessage(Component.text("Hold a named ominous trial key in your main hand or off hand."));
            return true;
        }
        
        // Access keyToChest via LockService (or DataStore via LockService)
        String locationKey = lockService.getKeyToChest().get(keyName);
        if (locationKey == null) {
            player.sendMessage(Component.text("No locked container found for key name: " + keyName));
            return true;
        }
        
        // Access lockedChests via LockService (or DataStore via LockService)
        LockInfo lockInfo = lockService.getLockedChests().get(locationKey);
        if (lockInfo == null) {
            player.sendMessage(Component.text("Lock data missing for key name: " + keyName));
            return true;
        }
        
        LocationData locationData = LocationUtil.parseLocationKey(locationKey); // Use LocationUtil
        String creator = lockInfo.creatorName() == null ? "unknown" : lockInfo.creatorName();
        String lastUser = lockInfo.lastUserName() == null ? "unknown" : lockInfo.lastUserName();
        player.sendMessage(Component.text("Key name: " + lockInfo.keyName()));
        if (locationData != null && locationData.realm() != null) {
            player.sendMessage(Component.text("Locked container: " + locationKey + " (" + locationData.realm() + ")"));
        } else {
            player.sendMessage(Component.text("Locked container: " + locationKey));
        }
        player.sendMessage(Component.text("Created by: " + creator));
        player.sendMessage(Component.text("Last used by: " + lastUser));
        return true;
    }
}
