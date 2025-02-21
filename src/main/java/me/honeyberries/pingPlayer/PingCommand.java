package me.honeyberries.pingPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Handles the /ping command to get player ping information
 */
public class PingCommand implements CommandExecutor, TabExecutor {

    /**
     * Executes the /ping command to check player ping
     *
     * @param sender the sender of the command
     * @param command the command being executed
     * @param label the alias used for the command
     * @param args the arguments provided with the command
     * @return true if the command was successfully executed, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            handlePingForSender(sender);
        } else if (args.length == 1) {
            handlePingForPlayer(sender, args[0]);
        } else if (args.length == 2) {
            handlePingRouterCommand(sender, args);
        } else {
            sendInvalidUsageMessage(sender);
        }
        return true;
    }

    /**
     * Handles the case where no arguments are provided and the sender is a player.
     * It checks the ping of the player sending the command.
     *
     * @param sender the sender of the command
     */
    private void handlePingForSender(CommandSender sender) {
        if (sender instanceof Player player) {
            sendPingMessage(sender, player.getName(), player.getPing());
        } else {
            sender.sendMessage(Component.text("You must be a player to use this command without arguments!", NamedTextColor.RED));
        }
    }

    /**
     * Handles the case where one argument (player name) is provided.
     * If it's a player name, it checks their ping.
     *
     * @param sender the sender of the command
     * @param playerName the player name
     */
    private void handlePingForPlayer(CommandSender sender, String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(Component.text("Player not found or offline. Please enter a valid username!", NamedTextColor.RED));
        } else {
            sendPingMessage(sender, player.getName(), player.getPing());
        }
    }

    /**
     * Handles the case where two arguments are provided.
     * The second argument specifies if the "router" ping test should be performed.
     *
     * @param sender the sender of the command
     * @param args the command arguments
     */
    private void handlePingRouterCommand(CommandSender sender, String[] args) {
        if (args[1].equalsIgnoreCase("router")) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(Component.text("Player not found or not online.", NamedTextColor.RED));
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(PingPlayer.getInstance(), new PingRouter(sender, target));
            }
        } else {
            sendInvalidUsageMessage(sender);
        }
    }


    /**
     * Sends an invalid usage message to the sender.
     *
     * @param sender the sender of the command
     */
    private void sendInvalidUsageMessage(CommandSender sender) {
        sender.sendMessage(Component.text("Invalid command syntax! \nUsage: /ping <playername>", NamedTextColor.RED));
    }

    /**
     * Sends a formatted ping message to the sender.
     *
     * @param sender the sender of the command
     * @param playerName the name of the player
     * @param ping the ping of the player
     */
    private void sendPingMessage(CommandSender sender, String playerName, int ping) {
        Component pingMessage = formatPingMessage(playerName, ping);
        sender.sendMessage(pingMessage);
    }

    /**
     * Formats the ping message with appropriate color based on ping value.
     *
     * @param playerName the name of the player
     * @param ping the ping value
     * @return the formatted ping message
     */
    private static @NotNull Component formatPingMessage(String playerName, int ping) {
        List<Integer> pingThresholds = PingSettings.getInstance().getPingThresholds();

        NamedTextColor color;
        String quality;

        if (ping < pingThresholds.get(0)) {
            color = NamedTextColor.GREEN;
            quality = "excellent";
        } else if (ping < pingThresholds.get(1)) {
            color = NamedTextColor.YELLOW;
            quality = "good";
        } else if (ping < pingThresholds.get(2)) {
            color = NamedTextColor.GOLD;
            quality = "ok";
        } else if (ping < pingThresholds.get(3)) {
            color = NamedTextColor.RED;
            quality = "bad";
        } else {
            color = NamedTextColor.DARK_RED;
            quality = "terrible";
        }

        return Component.text(playerName + "'s latency is ", NamedTextColor.GREEN)
                .append(Component.text(ping + " ms, which is " + quality + "!", color));
    }

    /**
     * Provides tab completion for player names and special commands.
     *
     * @param sender the sender of the command
     * @param command the command being executed
     * @param label the alias used for the command
     * @param args the command arguments
     * @return a list of tab completion suggestions
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .forEach(suggestions::add);

        } else if (args.length == 2) {
            suggestions.add("router");
        }

        return suggestions;
    }
}
