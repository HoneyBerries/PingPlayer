package me.honeyberries.pingPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class TabUpdateTask implements Runnable {

    private final PingPlayer plugin = PingPlayer.getInstance();
    private final PingSettings settings = PingSettings.getInstance();

    @Override
    public void run() {
        // Update the tab list for all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTabListName(player);
        }
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
        ChatColor color = getPingColor(ping);

        // Create the formatted tab name with ping in brackets and apply color
        String formattedTabName = playerName + color + " [" + ping + " ms]";

        // Set the player's tab name with the color and ping
        player.setPlayerListName(formattedTabName);
    }

    /**
     * Returns a color based on the ping value.
     *
     * @param ping The player's ping.
     * @return The color to use for the tab list name.
     */
    private ChatColor getPingColor(int ping) {
        List<Integer> pingThresholds = settings.getPingThresholds();

        // Determine color based on ping value
        if (ping <= pingThresholds.get(0)) {
            return ChatColor.GREEN; // Green for excellent ping
        } else if (ping <= pingThresholds.get(1)) {
            return ChatColor.YELLOW; // Yellow for good ping
        } else if (ping <= pingThresholds.get(2)) {
            return ChatColor.RED; // Red for medium ping
        } else {
            return ChatColor.DARK_RED; // Dark Red for bad ping
        }
    }
}
