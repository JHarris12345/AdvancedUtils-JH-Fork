package net.advancedplugins.utils;

import org.bukkit.Bukkit;

public class SchedulerUtils {

    /**
     * Runs a Runnable after a certain amount of ticks.
     *
     * @return The Task ID.
     */
    public static int runTaskLater(Runnable task, long delay) {
        return FoliaScheduler.runTaskLater(ASManager.getInstance(), task, delay).getTaskId();
    }

    /**
     * Runs a Runnable after 1 server tick.
     *
     * @return The Task ID.
     */
    public static int runTaskLater(Runnable task) {
        return runTaskLater(task, 1L);
    }

    /**
     * Runs a Runnable every x ticks.
     *
     * @param initialDelay The time to delay the first execution.
     * @param period       The period between successive executions.
     * @return The Task ID.
     */
    public static int runTaskTimer(Runnable task, long initialDelay, long period) {
        return FoliaScheduler.runTaskTimer(ASManager.getInstance(), task, initialDelay, period).getTaskId();
    }

    /**
     * Runs an Async Runnable every x ticks.
     *
     * @param initialDelay The time to delay the first execution.
     * @param period       The period between successive executions.
     * @return The Task ID.
     */
    public static int runTaskTimerAsync(Runnable task, long initialDelay, long period) {
        return FoliaScheduler.runTaskTimerAsynchronously(ASManager.getInstance(), task, initialDelay, period).getTaskId();
    }

    /**
     * Runs a Runnable at the next server tick.
     *
     * @return The Task ID.
     */
    public static int runTask(Runnable task) {
        return FoliaScheduler.runTask(ASManager.getInstance(), task).getTaskId();
    }


    /**
     * Runs a Runnable asynchronously.
     *
     * @return The Task ID.
     */
    public static int runTaskAsync(Runnable task) {
        return FoliaScheduler.runTaskAsynchronously(ASManager.getInstance(), task).getTaskId();
    }

}
