package com.itedya.kasixmc.utils;

import com.itedya.kasixmc.KasixMC;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ThreadUtil {
    public static int asyncRepeat(Runnable runnable, int period) {
        KasixMC plugin = KasixMC.getInstance();

        return Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, runnable, 0, period);
    }

    public static BukkitTask async(Runnable runnable) {
        KasixMC plugin = KasixMC.getInstance();

        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static int sync(Runnable runnable) {
        KasixMC plugin = KasixMC.getInstance();

        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable);
    }

    public static int syncDelay(Runnable runnable, int delay) {
        KasixMC plugin = KasixMC.getInstance();

        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
    }
}
