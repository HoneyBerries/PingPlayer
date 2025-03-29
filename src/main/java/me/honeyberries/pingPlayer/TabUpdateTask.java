package me.honeyberries.pingPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.List;

/**
 * A task that updates the tab list names of players based on their ping.
 * This task is run periodically to ensure the tab list is up-to-date.
 */
public class TabUpdateTask implements Runnable {

    private final PingPlayer plugin = PingPlayer.getInstance();
    private final PingSettings settings = PingSettings.getInstance();

    /**
     * The main logic of the task that updates the tab list names for all online players
     * who have the permission to view ping.
     */
    @Override
    public void run() {
        // Update the tab list for all online players with the permission to view ping
        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("pingplayer.viewping")).forEach(this::updateTabListName);
    }

    /**
     * Updates the player's tab list name based on their ping.
     *
     * @param player The player whose tab name is to be updated.
     */
    private void updateTabListName(Player player) {
        int ping = player.getPing();
        String playerName = player.getName();

        // Get the color based on the ping
        NamedTextColor color = getPingColor(ping);

        // Create the formatted tab name with ping in brackets and apply color
        Component formattedTabName = Component.text(playerName)
                .append(Component.text(" [" + ping + " ms]").color(color));

        // Set the player's tab name using the Adventure API
        if (settings.getShowPingOnTab()) {
            player.playerListName(formattedTabName);
        }
    }

    /**
     * Returns a color based on the ping value.
     *
     * @param ping The player's ping.
     * @return The color to use for the tab list name.
     */
    private NamedTextColor getPingColor(int ping) {
        List<Integer> pingThresholds = settings.getPingThresholds();

        // Determine color based on ping value
        if (ping <= pingThresholds.get(0)) {
            return NamedTextColor.GREEN; // Green for excellent ping
        } else if (ping <= pingThresholds.get(1)) {
            return NamedTextColor.YELLOW; // Yellow for good ping
        } else if (ping <= pingThresholds.get(2)) {
            return NamedTextColor.GOLD; // Gold for medium ping
        } else if (ping <= pingThresholds.get(3)) {
            return NamedTextColor.RED; // Red for bad ping
        } else {
            return NamedTextColor.DARK_RED; // Dark Red for very bad ping
        }
    }
}