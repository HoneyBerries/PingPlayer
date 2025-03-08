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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles the /ping command to get player ping information.
 */
public class PingCommand implements CommandExecutor, TabExecutor {

    private final PingPlayer plugin = PingPlayer.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender.hasPermission("pingplayer.ping"))) {
            sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            handlePingForSender(sender); // No arguments: check sender's ping
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendHelpMessage(sender);
            } else {
                handlePingForPlayer(sender, args[0]);
            }
        } else {
            sendHelpMessage(sender);
        }
        return true;
    }

    /**
     * Sends a help message detailing how to use the /ping command.
     *
     * @param sender the sender requesting help
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(Component.text("Usage:\n", NamedTextColor.AQUA)
                .append(Component.text("/ping - Check your own ping\n", NamedTextColor.GREEN))
                .append(Component.text("/ping <player> - Check another player's ping\n", NamedTextColor.YELLOW))
        );
    }

    /**
     * Handles the case where no arguments are provided and the sender is a player.
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
     * Sends a formatted ping message to the sender.
     *
     * @param sender the sender of the command
     * @param playerName the name of the player
     * @param ping the ping of the player
     */
    private void sendPingMessage(CommandSender sender, String playerName, int ping) {
        PingQuality pingQuality = getPingColorAndQuality(ping);

        Component pingMessage = Component.text(playerName + "'s latency is ", NamedTextColor.GREEN)
                .append(Component.text(ping + " ms, which is " + pingQuality.quality + "!", pingQuality.color));

        sender.sendMessage(pingMessage);
        plugin.getLogger().info(playerName + ": " + pingQuality.quality);
    }

    /**
     * Determines the color and quality description for a given ping value.
     *
     * @param ping the ping value
     * @return a PingQuality object containing the appropriate color and description
     */
    private PingQuality getPingColorAndQuality(int ping) {
        List<Integer> pingThresholds = PingSettings.getInstance().getPingThresholds();

        if (ping < pingThresholds.get(0)) return new PingQuality(NamedTextColor.GREEN, "excellent");
        if (ping < pingThresholds.get(1)) return new PingQuality(NamedTextColor.YELLOW, "good");
        if (ping < pingThresholds.get(2)) return new PingQuality(NamedTextColor.GOLD, "ok");
        if (ping < pingThresholds.get(3)) return new PingQuality(NamedTextColor.RED, "bad");

        return new PingQuality(NamedTextColor.DARK_RED, "terrible");
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
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return Stream.concat(
                            Bukkit.getOnlinePlayers().stream()
                                    .map(Player::getName),
                            Stream.of("help")
                    )
                    .filter(option -> option.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    /**
     * A simple record class to store ping quality information.
     */
    private record PingQuality(NamedTextColor color, String quality) {
    }
}
