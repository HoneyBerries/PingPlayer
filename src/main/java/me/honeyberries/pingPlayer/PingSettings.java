package me.honeyberries.pingPlayer;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages the configuration settings for the PingPlayer plugin.
 * This class follows the Singleton pattern to ensure only a single instance is used throughout the plugin.
 */
public class PingSettings {

    // Get plugin instance
    private static final PingPlayer plugin = PingPlayer.getInstance();

    // Singleton instance
    private static final PingSettings INSTANCE = new PingSettings();

    // Default values for configuration settings
    private static final List<Integer> DEFAULT_THRESHOLDS = Arrays.asList(50, 100, 200, 300);

    // Configuration file and settings
    private File configFile;
    private YamlConfiguration yamlConfig;
    private List<Integer> pingThresholds;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private PingSettings() {
    }

    /**
     * Gets the single instance of PingSettings.
     *
     * @return the instance of PingSettings
     */
    public static PingSettings getInstance() {
        return INSTANCE;
    }

    /**
     * Loads the configuration from the config.yml file.
     * If the config file does not exist, it is created from the plugin's resource.
     */
    public void load() {
        configFile = new File(PingPlayer.getInstance().getDataFolder(), "config.yml");

        // Check if the configuration file exists, if not, create it from the resource
        if (!configFile.exists()) {
            PingPlayer.getInstance().saveResource("config.yml", false);
        }

        // Load the configuration file
        yamlConfig = YamlConfiguration.loadConfiguration(configFile);
        yamlConfig.options().parseComments(true); // Enable comment parsing in the YAML file

        // Load specific configuration values
        loadPingThresholds();

        // Log the loaded configuration
        logConfiguration();
    }

    /**
     * Loads the ping latency thresholds from the configuration.
     * It retrieves four thresholds: excellent, good, medium, and bad.
     * If the values are invalid, default values are used.
     */
    private void loadPingThresholds() {
        try {
            pingThresholds = Stream.of(
                            yamlConfig.getInt("ping-thresholds.excellent", DEFAULT_THRESHOLDS.get(0)),
                            yamlConfig.getInt("ping-thresholds.good", DEFAULT_THRESHOLDS.get(1)),
                            yamlConfig.getInt("ping-thresholds.medium", DEFAULT_THRESHOLDS.get(2)),
                            yamlConfig.getInt("ping-thresholds.bad", DEFAULT_THRESHOLDS.get(3))
                    )
                    .sorted()
                    .collect(Collectors.toList());

            // Validate that thresholds are non-negative
            if (pingThresholds.stream().anyMatch(t -> t < 0)) {
                throw new IllegalArgumentException("Ping thresholds must be non-negative.");
            }
        } catch (Exception e) {
            // In case of an error, use default values
            PingPlayer.getInstance().getLogger().warning("Error loading ping thresholds! Using default values.");
            pingThresholds = DEFAULT_THRESHOLDS;
        }
    }

    /**
     * Logs the current configuration values for debugging and verification.
     * Outputs the loaded ping thresholds.
     */
    private void logConfiguration() {
        PingPlayer.getInstance().getLogger().info("Config loaded successfully!");
        PingPlayer.getInstance().getLogger().info("Latency thresholds: " +
                pingThresholds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    /**
     * Saves the current configuration to the config.yml file.
     * If the save fails, a warning message is logged.
     */
    public void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (IOException e) {
            PingPlayer.getInstance().getLogger().warning("Failed to save configuration file.");
        }
    }

    /**
     * Sets a value in the configuration and saves the updated configuration file.
     *
     * @param path  the configuration path
     * @param value the value to set
     */
    public void set(@NotNull String path, @NotNull Object value) {
        yamlConfig.set(path, value);
        saveConfig();
    }

    /**
     * Gets the list of ping latency thresholds from the configuration.
     * The list contains four values representing different latency levels:
     * - Excellent
     * - Good
     * - Medium
     * - Bad
     *
     * @return a sorted list of four integers representing latency levels
     */
    public List<Integer> getPingThresholds() {
        return pingThresholds;
    }

    /**
     * Sets the ping latency thresholds for the plugin.
     * The list must contain exactly four values, representing the thresholds for excellent, good, medium, and bad pings.
     *
     * @param pingThresholds a list of four integers representing the latency thresholds
     * @throws IllegalArgumentException if the list does not contain exactly four values
     */
    public void setPingThresholds(@NotNull List<Integer> pingThresholds) {
        if (pingThresholds.size() != 4) {
            throw new IllegalArgumentException("Ping times list must contain exactly 4 values.");
        }

        // Sort and set the new ping thresholds
        this.pingThresholds = pingThresholds.stream().sorted().collect(Collectors.toList());

        // Update the configuration file with the new values
        set("ping-thresholds.excellent", pingThresholds.get(0));
        set("ping-thresholds.good", pingThresholds.get(1));
        set("ping-thresholds.medium", pingThresholds.get(2));
        set("ping-thresholds.bad", pingThresholds.get(3));
    }
}
