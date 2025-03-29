package me.honeyberries.pingPlayer;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

/**
 * The main class for the PingPlayer plugin.
 * This plugin allows players to check the ping of other players and retrieve IP information.
 */
public final class PingPlayer extends JavaPlugin {

    private Task tabUpdateTask;

    /**
     * Called when the plugin is enabled.
     * Initializes the plugin, loads settings, and sets up commands and scheduled tasks.
     */
    @Override
    public void onEnable() {
        getLogger().info("PingPlayer has been enabled. You can ping players using /ping <playername>");

        // Load the plugin settings
        PingSettings.getInstance().load();

        // Set the command executors for the plugin commands
        Objects.requireNonNull(getServer().getPluginCommand("ping")).setExecutor(new PingCommand());
        Objects.requireNonNull(getServer().getPluginCommand("ip")).setExecutor(new IPCommand());
        Objects.requireNonNull(getServer().getPluginCommand("pingplayer")).setExecutor(new PingPlayerCommand());

        // Schedule the tab update task using the Scheduler class
        tabUpdateTask = Scheduler.runTaskTimer(() -> new TabUpdateTask().run(), 1, 1);
    }

    /**
     * Called when the plugin is disabled.
     * Cleans up any resources and cancels scheduled tasks.
     */
    @Override
    public void onDisable() {
        getLogger().info("PingPlayer has been disabled!");

        // Cancel the tab update task if it is running
        if (tabUpdateTask != null && !tabUpdateTask.isCancelled()) {
            tabUpdateTask.cancel();
        }
    }

    /**
     * Gets the instance of the PingPlayer plugin.
     *
     * @return the instance of the PingPlayer plugin
     */
    public static PingPlayer getInstance() {
        return getPlugin(PingPlayer.class);
    }
}