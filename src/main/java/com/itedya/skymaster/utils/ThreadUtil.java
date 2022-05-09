package com.itedya.skymaster.utils;

import com.itedya.skymaster.SkyMaster;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ThreadUtil {
    public static int asyncRepeat(Runnable runnable, int period) {
        SkyMaster plugin = SkyMaster.getInstance();

        return Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, runnable, 0, period);
    }

    public static BukkitTask async(Runnable runnable) {
        SkyMaster plugin = SkyMaster.getInstance();

        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static int sync(Runnable runnable) {
        SkyMaster plugin = SkyMaster.getInstance();

        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable);
    }

    public static int syncDelay(Runnable runnable, int delay) {
        SkyMaster plugin = SkyMaster.getInstance();

        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
    }
}
