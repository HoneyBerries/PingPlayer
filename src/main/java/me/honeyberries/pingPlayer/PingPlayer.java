package me.honeyberries.pingPlayer;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PingPlayer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("PingPlayer has been enabled. You can ping players using /ping <playername>");
        PingSettings.getInstance().load();
        Objects.requireNonNull(getServer().getPluginCommand("ping")).setExecutor(new PingCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        getLogger().info("PingPlayer has been disabled!");

    }

    public static PingPlayer getInstance() {
        return getPlugin(PingPlayer.class);
    }
}
