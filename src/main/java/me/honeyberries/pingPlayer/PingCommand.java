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

import java.net.InetAddress;
import java.util.*;

public class PingCommand implements CommandExecutor, TabExecutor {

    // Retrieves an online player by UUID or username
    private Player getOnlinePlayer(@NotNull String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getPlayer(uuid); // Returns null if player is offline
        } catch (IllegalArgumentException e) {
            return Bukkit.getPlayer(identifier); // Fallback to name lookup
        }
    }

    // Retrieves an offline player by UUID or username
    private OfflinePlayer getOfflinePlayer(@NotNull String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            return Bukkit.getOfflinePlayer(identifier);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Map<String, Integer> pingMap = new HashMap<>();

        // Handle no arguments
        if (args.length == 0) {
            if (sender instanceof Player player) {
                pingMap.put(player.getName(), player.getPing());
            } else {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command without arguments!");
                return true;
            }
        }

        // Handle one argument
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("pingplayer.reload")) {
                    PingSettings.getInstance().load();
                    sender.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to reload the configuration!");
                }
                return true;
            }

            Player player = getOnlinePlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player not found or offline. Please enter a valid username!");
                return true;
            }

            pingMap.put(player.getName(), player.getPing());
        }

        else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("router")) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage(ChatColor.RED + "Player not found or not online.");
                    return true;
                }

                // Run the ping task asynchronously
                Bukkit.getScheduler().runTaskAsynchronously(PingPlayer.getInstance(), new PingRouter(sender, target));

            }
            else sender.sendMessage(ChatColor.RED + "Invalid Command Syntax! \nUsage: /ping <playername> <router (optional)>");
        }

        // Handle invalid syntax
        else {
            sender.sendMessage(ChatColor.RED + "Invalid command syntax!\nUsage: /ping <playername> or /ping reload");
        }

        // Send ping results
        pingMap.forEach((name, ping) -> sender.sendMessage(formatPingMessage(name, ping)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            List<String> suggestions = new ArrayList<>();

            Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .forEach(suggestions::add);

            suggestions.add("reload");
            return suggestions;
        }

        if (args.length == 2)
            return List.of("router");

        return Collections.emptyList();
    }

    // Formats the ping message based on latency thresholds
    private static @NotNull String formatPingMessage(String playerName, int ping) {
        List<Integer> pingThresholds = PingSettings.getInstance().getPingTimes();

        ChatColor color;
        String quality;

        if (ping < pingThresholds.get(0)) {
            color = ChatColor.GREEN;
            quality = "excellent";
        } else if (ping < pingThresholds.get(1)) {
            color = ChatColor.YELLOW;
            quality = "good";
        } else if (ping < pingThresholds.get(2)) {
            color = ChatColor.GOLD;
            quality = "ok";
        } else if (ping < pingThresholds.get(3)) {
            color = ChatColor.RED;
            quality = "bad";
        } else {
            color = ChatColor.DARK_RED;
            quality = "terrible";
        }

        return ChatColor.GREEN + playerName + ChatColor.GOLD + "'s latency is " + color + ping + ChatColor.GOLD + " ms, which is " + quality + "!";
    }
}
