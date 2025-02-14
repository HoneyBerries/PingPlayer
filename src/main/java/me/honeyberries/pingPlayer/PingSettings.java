package me.honeyberries.pingPlayer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the configuration settings for the PingPlayer plugin.
 * This class follows the Singleton pattern to ensure a single instance.
 */
public class PingSettings {

    private static final PingSettings INSTANCE = new PingSettings();
    private File configFile;
    private YamlConfiguration yamlConfig;
    private List<Integer> pingThresholds;
    private boolean logIPs;

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
     */
    public void load() {
        configFile = new File(PingPlayer.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            PingPlayer.getInstance().saveResource("config.yml", false);
        }

        yamlConfig = YamlConfiguration.loadConfiguration(configFile);
        yamlConfig.options().parseComments(true);

        // Load ping latency settings with default values if missing
        pingThresholds = Arrays.asList(
            yamlConfig.getInt("ping-thresholds.excellent", 50),
            yamlConfig.getInt("ping-thresholds.good", 100),
            yamlConfig.getInt("ping-thresholds.medium", 200),
            yamlConfig.getInt("ping-thresholds.bad", 300)
        );

        // Load log IP setting with a default value if missing
        logIPs = yamlConfig.getBoolean("log-ip", true);

        PingPlayer.getInstance().getLogger().info(
                "Config loaded successfully! \nLatency thresholds: " +
            pingThresholds.stream().map(String::valueOf).collect(Collectors.joining(", ")) +
                "\nLog IPs: " + logIPs);
    }

    /**
     * Saves the current configuration to the config file.
     */
    public void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (IOException e) {
            PingPlayer.getInstance().getLogger().warning("Failed to save configuration file.");
        }
    }

    /**
     * Sets a value in the configuration and saves it.
     *
     * @param path  the configuration path
     * @param value the value to set
     */
    public void set(@NotNull String path, @NotNull Object value) {
        yamlConfig.set(path, value);
        saveConfig();
    }

    /**
     * Gets the list of ping latency thresholds.
     *
     * @return a list of four integer values representing latency levels
     */
    public List<Integer> getPingThresholds() {
        return pingThresholds;
    }

    /**
     * Checks if IP logging is enabled.
     *
     * @return true if logging is enabled, false otherwise
     */
    public boolean isLogIPs() {
        return logIPs;
    }

    /**
     * Sets whether IP logging should be enabled.
     *
     * @param logIPs true to enable logging, false to disable
     */
    public void setLogIPs(boolean logIPs) {
        this.logIPs = logIPs;
        set("log-ip", logIPs);
    }

    /**
     * Sets the latency thresholds for different ping levels.
     *
     * @param pingThresholds a list of four integer values representing latency levels
     * @throws IllegalArgumentException if the list does not contain exactly four values
     */
    public void setPingThresholds(@NotNull List<Integer> pingThresholds) {
        if (pingThresholds.size() != 4) {
            throw new IllegalArgumentException("Ping times list must contain exactly 4 values.");
        }

        this.pingThresholds = pingThresholds;

        set("ping-thresholds.excellent", pingThresholds.get(0));
        set("ping-thresholds.good", pingThresholds.get(1));
        set("ping-thresholds.medium", pingThresholds.get(2));
        set("ping-thresholds.bad", pingThresholds.get(3));
    }
}
