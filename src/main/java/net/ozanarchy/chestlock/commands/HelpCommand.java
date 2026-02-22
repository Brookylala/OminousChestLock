package net.ozanarchy.chestlock.commands;

import net.kyori.adventure.text.Component;
import net.ozanarchy.chestlock.ChestLockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {

    private final ChestLockPlugin plugin;

    public HelpCommand(ChestLockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("chestlock.admin")) { // Assuming help is for admin commands
            sender.sendMessage(Component.text("You do not have permission."));
            return true;
        }

        if (sender instanceof Player player) {
            sendHelp(player);
        } else {
            sender.sendMessage(Component.text("Usage: /chestlock <info|unlock|keyinfo|reload|loglevel|normalkeys|lockpicks|lockoutscope|give|help>"));
        }
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("/chestlock info - show lock key name for looked-at container"));
        player.sendMessage(Component.text("/chestlock unlock - force unlock looked-at container"));
        player.sendMessage(Component.text("/chestlock keyinfo - show lock info for key in hand"));
        player.sendMessage(Component.text("/chestlock reload - reload lock data from disk"));
        player.sendMessage(Component.text("/chestlock loglevel <0-3> - set log verbosity"));
        player.sendMessage(Component.text("/chestlock normalkeys <on|off> - allow normal trial keys"));
        player.sendMessage(Component.text("/chestlock lockpicks <on|off> - allow lock picking and crafting"));
        player.sendMessage(Component.text("/chestlock lockoutscope <chest|player> - set lockout scope"));
        player.sendMessage(Component.text("/chestlock give <player> <rusty|normal|silence> [amount] - give lock picks"));
        player.sendMessage(Component.text("/chestlock help - show this help message"));
    }
}
