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

    public static final PingSettings instance = new PingSettings();
    private File configFile;
    private YamlConfiguration yamlConfig;
    private ArrayList<Integer> pingTimes = new ArrayList<>();

    private PingSettings() {
        // singleton pattern
    }

    public static PingSettings getInstance() {
        return instance;
    }

    public void load() {
        this.configFile = new File(PingPlayer.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            PingPlayer.getInstance().saveResource("config.yml", false);
        }

        this.yamlConfig = new YamlConfiguration();
        yamlConfig.options().parseComments(true);

        try {
            yamlConfig.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Configuration File failed to be loaded ;(");
        }

        //try to write the latency timings to pingTimes

        try {
            pingTimes.clear();
            pingTimes.add(yamlConfig.getInt("ping-latency.excellent"));
            pingTimes.add(yamlConfig.getInt("ping-latency.good"));
            pingTimes.add(yamlConfig.getInt("ping-latency.medium"));
            pingTimes.add(yamlConfig.getInt("ping-latency.bad"));

        } catch (Exception e) {
            e.printStackTrace();
            this.pingTimes = new ArrayList<>(Arrays.asList(50, 100, 200, 300));
            Bukkit.getLogger().warning("failed to get the latency timings. " +
            "Defaulting to 50, 100, 200, and 300 starting from best to worse!");

        }

        Bukkit.getLogger().info("Successfully loaded the config! Latency timings are " + pingTimes.stream()
                .map(String::valueOf) // Convert each integer to a string
                .collect(Collectors.joining(", "))); // Join them with a comma and space
    }


    public void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Configuration File failed to be saved ;(");
        }
    }

    public void set(@NotNull String path, @NotNull Object value) {
        yamlConfig.set(path, value);
        saveConfig();
    }


    public ArrayList<Integer> getPingTimes() {
        return pingTimes;
    }

    public void setPingTimes(ArrayList<Integer> pingTimes) {
        this.pingTimes = pingTimes;

        // Set all the corresponding values from pingTimes list to the config
        PingSettings.getInstance().set("ping-latency.excellent", pingTimes.get(0));
        PingSettings.getInstance().set("ping-latency.good", pingTimes.get(1));
        PingSettings.getInstance().set("ping-latency.medium", pingTimes.get(2));
        PingSettings.getInstance().set("ping-latency.bad", pingTimes.get(3));

        saveConfig();
    }


}
