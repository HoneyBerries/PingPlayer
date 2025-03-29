package me.honeyberries.pingPlayer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class Scheduler {

    private static boolean isFolia;
    private static final Plugin plugin = PingPlayer.getInstance();

    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (final ClassNotFoundException e) {
            isFolia = false;
        }
    }

    /**
     * Runs a task on the main server thread.
     *
     * @param runnable the task to be run
     */
    public static void runTask(Runnable runnable) {
        try {
            if (isFolia)
                Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
            else
                Bukkit.getScheduler().runTask(plugin, runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs a task asynchronously.
     *
     * @param runnable the task to be run
     */
    public static void runTaskAsynchronously(Runnable runnable) {
        try {
            if (isFolia)
                Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
            else
                Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Schedules a task to run after a specified delay.
     *
     * @param runnable the task to be run
     * @param delayTicks the delay in ticks before the task is run
     * @return a Task representing the scheduled task
     */
    public static Task runTaskLater(Runnable runnable, long delayTicks) {
        try {
            if (isFolia)
                return new Task(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> runnable.run(), delayTicks));
            else
                return new Task(Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Schedules a task to run asynchronously after a specified delay.
     *
     * @param runnable the task to be run
     * @param delayTicks the delay in ticks before the task is run
     * @return a Task representing the scheduled task
     */
    public static Task runTaskLaterAsynchronously(Runnable runnable, long delayTicks) {
        try {
            if (isFolia)
                return new Task(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> runnable.run(), delayTicks));
            else
                return new Task(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Schedules a task to run repeatedly with a fixed delay between executions.
     *
     * @param runnable the task to be run
     * @param delayTicks the delay in ticks before the first execution
     * @param periodTicks the period in ticks between subsequent executions
     * @return a Task representing the scheduled task
     */
    public static Task runTaskTimer(Runnable runnable, long delayTicks, long periodTicks) {
        try {
            if (isFolia)
                // Use folia task
                return new Task(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> runnable.run(), delayTicks < 1 ? 1 : delayTicks, periodTicks < 1 ? 1 : periodTicks));
            else
                // Use bukkit task
                return new Task(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Schedules a task to run asynchronously and repeatedly with a fixed delay between executions.
     *
     * @param runnable the task to be run
     * @param delayTicks the delay in ticks before the first execution
     * @param periodTicks the period in ticks between subsequent executions
     * @return a Task representing the scheduled task
     */
    public static Task runTaskTimerAsynchronously(Runnable runnable, long delayTicks, long periodTicks) {
        try {
            if (isFolia)
                return new Task(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> runnable.run(), delayTicks < 1 ? 1 : delayTicks, periodTicks));
            else
                return new Task(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delayTicks, periodTicks));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if the server is running Folia.
     *
     * @return true if the server is running Folia, false otherwise
     */
    public static boolean isFolia() {
        return isFolia;
    }
}
