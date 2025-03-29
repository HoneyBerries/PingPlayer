package me.honeyberries.pingPlayer;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.scheduler.BukkitTask;

/**
 * Represents a task that can be scheduled and cancelled.
 */
public class Task {

    private ScheduledTask foliaTask;
    private BukkitTask bukkitTask;

    /**
     * Constructs a Task with a ScheduledTask.
     *
     * @param foliaTask the ScheduledTask to be managed by this Task
     */
    public Task(ScheduledTask foliaTask) {
        this.foliaTask = foliaTask;
    }

    /**
     * Constructs a Task with a BukkitTask.
     *
     * @param bukkitTask the BukkitTask to be managed by this Task
     */
    public Task(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    /**
     * Cancels the task if it is not already cancelled.
     */
    public void cancel() {
        try {
            if (foliaTask != null)
                foliaTask.cancel();
            else if (bukkitTask != null)
                bukkitTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the task is cancelled.
     *
     * @return true if the task is cancelled, false otherwise
     */
    public boolean isCancelled() {
        if (foliaTask != null)
            return foliaTask.isCancelled();
        else if (bukkitTask != null)
            return bukkitTask.isCancelled();
        return true;
    }
}
