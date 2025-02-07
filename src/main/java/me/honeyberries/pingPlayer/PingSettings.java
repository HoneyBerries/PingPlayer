package me.honeyberries.pingPlayer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PingSettings {

    private static final PingSettings INSTANCE = new PingSettings();
    private File configFile;
    private YamlConfiguration yamlConfig;
    private List<Integer> pingTimes = new ArrayList<>();

    private PingSettings() {
        // Singleton pattern
    }

    public static PingSettings getInstance() {
        return INSTANCE;
    }

    public void load() {
        configFile = new File(PingPlayer.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            PingPlayer.getInstance().saveResource("config.yml", false);
        }

        yamlConfig = YamlConfiguration.loadConfiguration(configFile);
        yamlConfig.options().parseComments(true);

        try {
            pingTimes = Arrays.asList(
                    yamlConfig.getInt("ping-latency.excellent"),
                    yamlConfig.getInt("ping-latency.good"),
                    yamlConfig.getInt("ping-latency.medium"),
                    yamlConfig.getInt("ping-latency.bad")
            );
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load latency timings. Defaulting to [50, 100, 200, 300].");
            pingTimes = Arrays.asList(50, 100, 200, 300);
        }

        Bukkit.getLogger().info("Config loaded successfully! Latency timings: " +
                pingTimes.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    public void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to save configuration file.");
        }
    }

    public void set(@NotNull String path, @NotNull Object value) {
        yamlConfig.set(path, value);
        saveConfig();
    }

    public List<Integer> getPingTimes() {
        return new ArrayList<>(pingTimes);
    }

    public void put(List<Integer> pingTimes) {
        if (pingTimes.size() != 4) {
            throw new IllegalArgumentException("Ping times list must contain exactly 4 values.");
        }

        this.pingTimes = new ArrayList<>(pingTimes);

        set("ping-latency.excellent", pingTimes.get(0));
        set("ping-latency.good", pingTimes.get(1));
        set("ping-latency.medium", pingTimes.get(2));
        set("ping-latency.bad", pingTimes.get(3));
    }
}
