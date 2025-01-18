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

            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("pingPlayer.reload")) {
                    PingSettings.getInstance().load();
                    sender.sendMessage(ChatColor.GREEN + "Reloaded the config!");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "You don't have the permission to reload the config!");
                }
                return true;
            }

            else {
                Player player = getOnlinePlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found or is offline. Enter a valid username!");
                    return true;
                }

                else {
                    pingMaps.put(player.getName(), player.getPing());
                }

            }

        }

        else {
            sender.sendMessage(ChatColor.RED + "Invalid command syntax! \n " +
            "Usage: /ping <playername> or /ping reload");
            return true;
        }

        if (pingMaps.size() == 1) {
            for (Map.Entry<String, Integer> entry : pingMaps.entrySet()) {
                //get the player username and latency value
                String message = getMessage(entry);

                // now send the message to the player
                sender.sendMessage(message);
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

        if (args.length == 1) { //name of a player
            String partialPlayerName = args[0];
            List<String> matchingNames = new ArrayList<>();

            //get the list of player suggestions
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                    matchingNames.add(player.getName()); //returns the suggestion for the players
                }
            }
            matchingNames.add("reload");
            return matchingNames;

        }

        else {
            return List.of();
        }

    }

    private static @NotNull String getMessage(Map.Entry<String, Integer> entry) {
        String key = entry.getKey();
        Integer value = entry.getValue();

        //get the ping timings for decision-making
        List<Integer> pingTimings = PingSettings.getInstance().getPingTimes();

        //change the color of the message depending on the quality of the connection!
        String message;
        if (value < pingTimings.get(0)) {
            message = ChatColor.GRAY + key + "'s latency is " + ChatColor.GREEN + value + ChatColor.GRAY + " ms, which is excellent!";
        }
        else if (value < pingTimings.get(1)) {
            message = ChatColor.GRAY + key + "'s latency is " + ChatColor.YELLOW + value + ChatColor.GRAY + " ms, which is good!";
        }
        else if (value < pingTimings.get(2)) {
            message = ChatColor.GRAY + key + "'s latency is " + ChatColor.GOLD + value + ChatColor.GRAY + " ms, which is ok!";
        }
        else if (value < pingTimings.get(3)) {
            message = ChatColor.GRAY + key + "'s latency is " + ChatColor.RED + value + ChatColor.GRAY + " ms, which is bad!";
        }
        else {
            message = ChatColor.GRAY + key + "'s latency is " + ChatColor.DARK_RED + value + ChatColor.GRAY + " ms, which is terrible!";
        }
        return message;
    }

}
