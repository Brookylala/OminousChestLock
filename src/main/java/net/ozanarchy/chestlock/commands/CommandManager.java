package net.ozanarchy.chestlock.commands;

import net.kyori.adventure.text.Component;
import net.ozanarchy.chestlock.ChestLockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final ChestLockPlugin plugin;
    private final Map<String, CommandExecutor> executors = new HashMap<>();
    private final Map<String, TabCompleter> completers = new HashMap<>();

    public CommandManager(ChestLockPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(String name, CommandExecutor executor, TabCompleter completer) {
        executors.put(name.toLowerCase(), executor);
        completers.put(name.toLowerCase(), completer);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("chestlock")) {
            return false;
        }

        if (args.length == 0) {
            // Default help if no subcommand is provided
            if (executors.containsKey("help")) {
                return executors.get("help").onCommand(sender, command, label, args);
            }
            sender.sendMessage(Component.text("Usage: /chestlock <subcommand> [args...]"));
            return true;
        }

        String sub = args[0].toLowerCase();
        CommandExecutor executor = executors.get(sub);

        if (executor == null) {
            sender.sendMessage(Component.text("Unknown subcommand: " + sub));
            if (executors.containsKey("help")) {
                return executors.get("help").onCommand(sender, command, label, args);
            }
            return false;
        }

        // Pass the remaining arguments to the subcommand executor
        String[] subcommandArgs = Arrays.copyOfRange(args, 1, args.length);
        return executor.onCommand(sender, command, label, subcommandArgs);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("chestlock")) {
            return List.of();
        }

        if (args.length == 1) {
            return executors.keySet().stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        String sub = args[0].toLowerCase();
        TabCompleter completer = completers.get(sub);

        if (completer == null) {
            return List.of();
        }

        // Pass the remaining arguments to the subcommand tab completer
        String[] subcommandArgs = Arrays.copyOfRange(args, 1, args.length);
        return completer.onTabComplete(sender, command, alias, subcommandArgs);
    }
}
