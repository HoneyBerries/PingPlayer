package me.honeyberries.pingPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PingCommand implements CommandExecutor, TabExecutor {

    // Get online player by UUID or username
    public Player getOnlinePlayer(@NotNull String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getPlayer(uuid); // Returns null if player is offline
        } catch (IllegalArgumentException e) {
            return Bukkit.getPlayer(identifier); // Fallback to name lookup
        }
    }

    // Get offline player by UUID or username
    public OfflinePlayer getOfflinePlayer(@NotNull String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            return Bukkit.getOfflinePlayer(identifier);
        }
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Map<String, Integer> pingMaps = new HashMap<>();

        if (args.length == 0) {
            if (sender instanceof Player player) {
                pingMaps.put(player.getName(), player.getPing());
            }
            else {
                sender.sendMessage("You need to be a player to use this command without any arguments!");
                return true;
            }
        }

        else if (args.length == 1) {

            Player player = getOnlinePlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player not found or is offline. Enter a valid username!");
                return true;

            }
            else {
                pingMaps.put(player.getName(), player.getPing());

            }
        }

        else {
            sender.sendMessage(ChatColor.RED + "Invalid command syntax!");
            sender.sendMessage("Usage: /ping <playername>");
            return true;
        }

        if (pingMaps.size() == 1) {
            for (Map.Entry<String, Integer> entry : pingMaps.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                sender.sendMessage(ChatColor.GREEN + key + "'s latency is " + value + "ms!");
                return true;
            }
        }
        else {
            return false;
        }


    return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            String partialPlayerName = args[0];
            List<String> matchingNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                    matchingNames.add(player.getName()); //returns the suggestion for the players
                }
            }
            return matchingNames;
        }

        else {

            return List.of();
        }

    }
}
