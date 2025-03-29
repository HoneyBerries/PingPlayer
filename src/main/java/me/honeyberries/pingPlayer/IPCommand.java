package me.honeyberries.pingPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Command handler for the /ip command.
 * This command reveals the IP address of an online player.
 * Also handles the /ip help command to display usage information.
 */
public class IPCommand implements CommandExecutor, TabExecutor {

    private final PingPlayer plugin = PingPlayer.getInstance();

    /**
     * Executes the /ip command to display the IP address of a specified player or the help message.
     *
     * @param sender  the sender of the command
     * @param command the command that was executed
     * @param label   the alias used for the command
     * @param args    the arguments passed to the command
     * @return true if the command was successfully executed, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check for permission
        if (!sender.hasPermission("pingplayer.ip")) {
            sender.sendMessage(Component.text("You don't have permission to view player IPs.", NamedTextColor.RED));
            return true;
        }

        // If no arguments or the first argument is "help", send the help message
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sendHelpMessage(sender);
            return true;
        }

        // If the number of arguments is not 1, show usage message
        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /ip <player> or /ip help", NamedTextColor.RED));
            return true;
        }

        // Attempt to get the target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(Component.text("Player not found or not online.", NamedTextColor.RED));
            return true;
        }

        // Check if target.getAddress() is null
        if (target.getAddress() == null) {
            sender.sendMessage(Component.text("Could not retrieve IP address for " + target.getName(), NamedTextColor.RED));
            return true;
        }

        // Get and format the IP address
        String ipAddress = target.getAddress().getAddress().getHostAddress();

        Component message = Component.text(target.getName() + "'s IP address is: ", NamedTextColor.GOLD)
                .append(Component.text(ipAddress, NamedTextColor.AQUA)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy IP", NamedTextColor.GREEN)))
                        .clickEvent(ClickEvent.copyToClipboard(ipAddress)));

        sender.sendMessage(message);
        return true;
    }

    /**
     * Sends a help message to the command sender, explaining how to use the /ip command.
     *
     * @param sender The command sender to whom the help message will be sent.
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(Component.text("----- IP Command Help -----", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/ip <player>", NamedTextColor.AQUA)
                .append(Component.text(" - Displays the IP address of the specified player.", NamedTextColor.GOLD)));
        sender.sendMessage(Component.text("/ip help", NamedTextColor.AQUA)
                .append(Component.text(" - Displays this help message.", NamedTextColor.GOLD)));
    }

    /**
     * Handles tab completion for the /ip command.
     * Provides player names and the "help" argument as suggestions based on the input.
     *
     * @param sender  the sender of the command
     * @param command the command that was executed
     * @param label   the alias used for the command
     * @param args    the arguments passed to the command
     * @return a list of suggested player names
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return Stream.concat(
                            Bukkit.getOnlinePlayers().stream().map(Player::getName),
                            Stream.of("help")
                    )
                    .filter(option -> option.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
