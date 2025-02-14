package me.honeyberries.pingPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;


public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {

        Player player = event.getPlayer();
        String ipAddress = event.getAddress().getHostAddress();

        if (PingSettings.getInstance().isLogIPs()) {
            PingPlayer.getInstance().getLogger().info(
                    ChatColor.GREEN + player.getName() + "'s IP is " + ipAddress);
        }

    }
}
