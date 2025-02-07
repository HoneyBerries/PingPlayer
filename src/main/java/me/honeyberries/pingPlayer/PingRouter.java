package me.honeyberries.pingPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.Objects;

public class PingRouter implements Runnable {

    private final CommandSender sender;
    private final Player target;

    // Constructor to pass sender and target
    public PingRouter(CommandSender sender, Player target) {
        this.sender = sender;
        this.target = target;
    }

    @Override
    public void run() {
        try {
            InetAddress address = Objects.requireNonNull(target.getAddress()).getAddress();
            int attempts = 4;
            long totalPing = 0;
            int successfulPings = 0;

            for (int i = 0; i < attempts; i++) {
                long startTime = System.currentTimeMillis();
                boolean reachable = address.isReachable(3000); // Timeout: 3 seconds
                long endTime = System.currentTimeMillis();

                if (reachable) {
                    totalPing += (endTime - startTime);
                    successfulPings++;
                }

                Thread.sleep(10); // Wait between pings to simulate real behavior
            }

            long averagePing = (successfulPings > 0) ? (totalPing / successfulPings) : -1;

            // Send the result back to the main thread
            Bukkit.getScheduler().runTask(PingPlayer.getInstance(), () -> {
                if (averagePing != -1) {
                    sender.sendMessage(ChatColor.GOLD + "Average ping to " + ChatColor.GREEN + target.getName() + ChatColor.GOLD + "'s router: " + ChatColor.DARK_PURPLE + averagePing + ChatColor.GOLD + " ms");
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to reach " + ChatColor.GREEN + target.getName() + ChatColor.RED + "'s router after " + attempts + " attempts.");
                }
            });
        } catch (Exception e) {
            Bukkit.getScheduler().runTask(PingPlayer.getInstance(), () -> {
                sender.sendMessage(ChatColor.RED + "An error occurred while pinging.");
            });
            e.printStackTrace();
        }
    }
}
