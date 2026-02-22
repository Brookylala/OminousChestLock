package net.ozanarchy.chestlock.commands;

import net.kyori.adventure.text.Component;
import net.ozanarchy.chestlock.ChestLockPlugin;
import net.ozanarchy.chestlock.lock.PickType;
import net.ozanarchy.chestlock.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GiveCommand implements CommandExecutor, TabCompleter {

    private final ChestLockPlugin plugin;

    public GiveCommand(ChestLockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("chestlock.admin")) { // Original had admin permission for give
            sender.sendMessage(Component.text("You do not have permission."));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Component.text("Usage: /chestlock give <player> <rusty|normal|silence|lodestone> [amount]"));
            return true;
        }

        String playerName = args[0];
        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found: " + playerName));
            return true;
        }

        PickType pickType = parsePickType(args[1]);
        if (pickType == null) {
            sender.sendMessage(Component.text("Pick type must be rusty, normal, silence, or lodestone."));
            return true;
        }

        int amount = 1;
        if (args.length >= 3) { // args[2] would be amount if present
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("Amount must be a number."));
                return true;
            }
        }
        if (amount <= 0) {
            sender.sendMessage(Component.text("Amount must be at least 1."));
            return true;
        }

        // Use ItemUtil to create the pick
        ItemStack stack = ItemUtil.createPick(pickType);
        stack.setAmount(amount);

        Map<Integer, ItemStack> overflow = target.getInventory().addItem(stack);
        for (ItemStack extra : overflow.values()) {
            target.getWorld().dropItemNaturally(target.getLocation(), extra);
        }
        sender.sendMessage(Component.text("Gave " + amount + " " + pickType.id() + " pick(s) to " + target.getName() + "."));
        return true;
    }

    private PickType parsePickType(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.toLowerCase();
        if (normalized.endsWith("_pick")) {
            normalized = normalized.substring(0, normalized.length() - "_pick".length());
        }
        return switch (normalized) {
            case "rusty" -> PickType.RUSTY;
            case "normal" -> PickType.NORMAL;
            case "silence" -> PickType.SILENCE;
            case "lodestone" -> PickType.LODESTONE;
            default -> null;
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("chestlock.admin")) {
            return List.of();
        }

        if (args.length == 1) { // Player name
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return names;
        }
        if (args.length == 2) { // Pick type
            return List.of("rusty", "normal", "silence", "lodestone");
        }
        if (args.length == 3) { // Amount
            return List.of("1", "5", "10", "64"); // Common amounts
        }
        return List.of();
    }
}
