package me.honeyberries.pingPlayer;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages the configuration settings for the PingPlayer plugin.
 * This class follows the Singleton pattern to ensure a single instance.
 */
public class PingSettings {

    private static final PingSettings INSTANCE = new PingSettings();
    private File configFile;
    private YamlConfiguration yamlConfig;
    private List<Integer> pingThresholds;
    private int packets;
    private int timeout;

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
        try {
            pingThresholds = Stream.of(
                    yamlConfig.getInt("ping-thresholds.excellent"),
                    yamlConfig.getInt("ping-thresholds.good"),
                    yamlConfig.getInt("ping-thresholds.medium"),
                    yamlConfig.getInt("ping-thresholds.bad")
            ).sorted().collect(Collectors.toList());

            for (int timing : pingThresholds) {
                if (timing < 0) {
                    throw new IllegalArgumentException("Ping thresholds must be positive and in-order!");
                }

            }
        } catch (Exception e) {
            PingPlayer.getInstance().getLogger().warning("Error loading ping thresholds! " +
                    "Defaulting to 50, 100, 200, and 300 respectively!");
        }

        try {
            packets = yamlConfig.getInt("packets");
            timeout = yamlConfig.getInt("timeout");
        } catch (Exception e) {
            PingPlayer.getInstance().getLogger().warning("Error getting packets and timeout values from config! Defaulting to 4 packets and 3000ms timeout!");
            packets = 4;
            timeout = 3000;
        }


        PingPlayer.getInstance().getLogger().info("Config loaded successfully!");
        PingPlayer.getInstance().getLogger().info("Latency thresholds: " +
        pingThresholds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        PingPlayer.getInstance().getLogger().info("Packets: " + packets);
        PingPlayer.getInstance().getLogger().info("Timeout: " + timeout + " ms");




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

    public int getPackets() {
        return packets;
    }

    public void setPackets(int packets) {
        this.packets = packets;
        set("packets", packets);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
        set("timeout", timeout);
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
