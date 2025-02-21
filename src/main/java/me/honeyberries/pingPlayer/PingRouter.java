package me.honeyberries.pingPlayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.Objects;

/**
 * This class is responsible for pinging a target player's computer multiple times
 * and calculating the average ping. Results are then sent to the sender of the command.
 */
public class PingRouter implements Runnable {

    private final CommandSender sender;
    private final Player target;

    /**
     * Constructs a PingRouter instance.
     *
     * @param sender the sender of the command
     * @param target the player to ping
     */
    public PingRouter(CommandSender sender, Player target) {
        this.sender = sender;
        this.target = target;
    }

    /**
     * Runs the ping test and calculates the average ping.
     * If successful, the result is sent to the sender. If the ping fails,
     * an error message is sent.
     */
    @Override
    public void run() {
        InetAddress address = getTargetAddress();
        if (address == null) {
            sendErrorMessage("Unable to retrieve IP address for " + target.getName());
            return;
        }

        int attempts = PingSettings.getInstance().getPackets();
        long totalPing = 0;
        int successfulPings = 0;
        int timeout = PingSettings.getInstance().getTimeout();

        for (int i = 0; i < attempts; i++) {
            if (pingAddress(address, timeout)) {
                long pingTime = getPingTime(address);
                if (pingTime >= 0) {
                    totalPing += pingTime;
                    successfulPings++;
                }
            }
            sleepBetweenPings();
        }

        sendPingResult(successfulPings, totalPing, attempts);
    }

    /**
     * Pings the target address and returns whether it is reachable within the timeout.
     *
     * @param address the InetAddress of the target
     * @param timeout the timeout in milliseconds
     * @return true if the address is reachable, false otherwise
     */
    private boolean pingAddress(InetAddress address, int timeout) {
        try {
            return address.isReachable(timeout);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Measures the ping time for the given address.
     *
     * @param address the InetAddress of the target
     * @return the ping time in milliseconds, or -1 if an error occurs
     */
    private long getPingTime(InetAddress address) {
        long startTime = System.currentTimeMillis();
        boolean reachable = pingAddress(address, PingSettings.getInstance().getTimeout());
        long endTime = System.currentTimeMillis();

        return reachable ? endTime - startTime : -1;
    }

    /**
     * Pauses the thread for a short period between pings.
     */
    private void sleepBetweenPings() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Sends the ping result to the command sender.
     *
     * @param successfulPings the number of successful pings
     * @param totalPing the total ping time for all successful pings
     * @param attempts the total number of attempts
     */
    private void sendPingResult(int successfulPings, long totalPing, int attempts) {
        Bukkit.getScheduler().runTask(PingPlayer.getInstance(), () -> {
            if (successfulPings > 0) {
                long averagePing = totalPing / successfulPings;
                sender.sendMessage(Component.text("Average ping to ", NamedTextColor.GOLD)
                        .append(Component.text(target.getName(), NamedTextColor.GREEN))
                        .append(Component.text("'s computer: ", NamedTextColor.GOLD))
                        .append(Component.text(averagePing + " ms", NamedTextColor.DARK_PURPLE))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy", NamedTextColor.YELLOW)))
                        .clickEvent(ClickEvent.copyToClipboard(String.valueOf(averagePing))));
            } else {
                sender.sendMessage(Component.text("Failed to reach ", NamedTextColor.RED)
                        .append(Component.text(target.getName(), NamedTextColor.GREEN))
                        .append(Component.text("'s computer after " + attempts + " attempts.", NamedTextColor.RED)));
            }
        });
    }

    /**
     * Sends an error message to the sender.
     *
     * @param message the message to send
     */
    private void sendErrorMessage(String message) {
        Bukkit.getScheduler().runTask(PingPlayer.getInstance(), () -> {
            sender.sendMessage(Component.text(message, NamedTextColor.RED));
        });
    }

    /**
     * Retrieves the InetAddress of the target player.
     *
     * @return the InetAddress of the target, or null if it cannot be retrieved
     */
    private InetAddress getTargetAddress() {
        try {
            return Objects.requireNonNull(target.getAddress()).getAddress();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
