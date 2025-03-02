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

/**
 * Command handler for the /ip command.
 * This command reveals the IP address of an online player.
 */
public class IPCommand implements CommandExecutor, TabExecutor {

    /**
     * Executes the /ip command to display the IP address of a specified player.
     *
     * @param sender the sender of the command
     * @param command the command that was executed
     * @param label the alias used for the command
     * @param args the arguments passed to the command
     * @return true if the command was successfully executed, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check for permission (optional)
        if (!sender.hasPermission("pingplayer.ip")) {
            sender.sendMessage(Component.text("You don't have permission to view player IPs.", NamedTextColor.RED));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /ip <player>", NamedTextColor.RED));
            return true;
        }

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

        String ipAddress = target.getAddress().getAddress().getHostAddress();

        Component message = Component.text(target.getName() + "'s IP address is: ", NamedTextColor.GOLD)
                .append(Component.text(ipAddress, NamedTextColor.AQUA)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy IP", NamedTextColor.YELLOW)))
                        .clickEvent(ClickEvent.copyToClipboard(ipAddress)));

        sender.sendMessage(message);
        return true;
    }

    /**
     * Handles tab completion for the /ip command.
     * Provides player names as suggestions based on the input.
     *
     * @param sender the sender of the command
     * @param command the command that was executed
     * @param label the alias used for the command
     * @param args the arguments passed to the command
     * @return a list of suggested player names
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
