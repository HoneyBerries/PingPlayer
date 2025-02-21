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
 * Handles the /pingplayer reload command to reload the configuration.
 */
public class PingPlayerCommand implements CommandExecutor, TabExecutor {

    /**
     * Executes the /pingplayer reload command to reload the configuration.
     *
     * @param sender the sender of the command
     * @param command the command being executed
     * @param label the alias used for the command
     * @param args the arguments provided with the command
     * @return true if the command was successfully executed, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("pingplayer.pingplayer")) {
            PingSettings.getInstance().load();
            sender.sendMessage(Component.text("Configuration reloaded successfully!", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("You do not have permission to reload the configuration!", NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("reload").filter(option -> option.startsWith(args[0].toLowerCase())).toList();
        }
        else return List.of();
    }
}
