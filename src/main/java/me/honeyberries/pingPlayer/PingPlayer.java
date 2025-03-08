package me.honeyberries.pingPlayer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

/**
 * The main class for the PingPlayer plugin.
 * This plugin allows players to check the ping of other players and retrieve IP information.
 */
public final class PingPlayer extends JavaPlugin {

    private BukkitTask tabUpdateTask;
    /**
     * Called when the plugin is enabled.
     * This method handles the startup logic, such as loading configuration settings
     * and registering commands.
     */
    @Override
    public void onEnable() {
        // Log message when the plugin is enabled
        getLogger().info("PingPlayer has been enabled. You can ping players using /ping <playername>");

        // Load the plugin configuration
        PingSettings.getInstance().load();

        // Register commands
        Objects.requireNonNull(getServer().getPluginCommand("ping")).setExecutor(new PingCommand());
        Objects.requireNonNull(getServer().getPluginCommand("ip")).setExecutor(new IPCommand());
        Objects.requireNonNull(getServer().getPluginCommand("pingplayer")).setExecutor(new PingPlayerCommand());

        // Schedule the tab update task to run every second (20 ticks)
        tabUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                new TabUpdateTask().run();
            }
        }.runTaskTimer(this, 0L, 0L); // Correct scheduling method
    }

    /**
     * Called when the plugin is disabled.
     * This method handles any necessary cleanup before the plugin shuts down.
     */
    @Override
    public void onDisable() {
        // Log message when the plugin is disabled
        getLogger().info("PingPlayer has been disabled!");

        if (tabUpdateTask != null && tabUpdateTask.isCancelled()) {
            tabUpdateTask.cancel();
        }

    }

    /**
     * Gets the instance of the PingPlayer plugin.
     *
     * @return The PingPlayer plugin instance.
     */
    public static PingPlayer getInstance() {
        return getPlugin(PingPlayer.class);
    }
}
