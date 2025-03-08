package me.honeyberries.pingPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

/**
 * Handles the /pingplayer command, providing administrative functions such as configuration reload and help.
 */
public class PingPlayerCommand implements CommandExecutor, TabExecutor {

    private final PingPlayer plugin = PingPlayer.getInstance();

    /**
     * Executes the /pingplayer command.
     * Supports subcommands like "reload" to reload the configuration, or "help" for command usage information.
     *
     * @param sender  the sender of the command
     * @param command the command being executed
     * @param label   the alias used for the command
     * @param args    the arguments provided with the command
     * @return true if the command was successfully executed, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender.hasPermission("pingplayer.settings"))) {
            sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload":
                handleReloadCommand(sender);
                break;
            case "help":
            default:
                sendHelpMessage(sender);
                break;
        }
        return true;
    }

    /**
     * Handles the "reload" subcommand of /pingplayer.
     * Reloads the configuration and sends a success or permission error message.
     *
     * @param sender the sender of the command
     */
    private void handleReloadCommand(CommandSender sender) {
        sender.hasPermission("pingplayer.settings");
        PingSettings.getInstance().load();
        sender.sendMessage(Component.text("Configuration reloaded successfully!", NamedTextColor.GREEN));
    }

    /**
     * Sends the help message to the command sender.
     * Displays the available subcommands and their usage.
     *
     * @param sender the sender of the command
     */
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(Component.text("----- PingPlayer Help -----", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/pingplayer reload", NamedTextColor.AQUA)
                .append(Component.text(" - Reloads the plugin configuration.", NamedTextColor.GOLD)));
        sender.sendMessage(Component.text("/pingplayer help", NamedTextColor.AQUA)
                .append(Component.text(" - Displays this help message.", NamedTextColor.GOLD)));
    }

    /**
     * Provides tab completion for the /pingplayer command.
     * Suggests "reload" and "help" as subcommands.
     *
     * @param commandSender the sender of the command
     * @param command       the command being executed
     * @param s             the alias used for the command (unused)
     * @param args          the arguments provided with the command
     * @return a list of tab completion suggestions
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("reload", "help")
                    .filter(option -> option.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
