package com.itedya.kasixmc.utils;

import com.itedya.kasixmc.chunkgenerators.VoidChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class WorldUtil {
    public static boolean doesWorldExists(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return world != null;
    }

    public static void createVoidWorld(String worldName) {
        WorldCreator wc = new WorldCreator(worldName);
        wc.generator(new VoidChunkGenerator());
        wc.createWorld();
    }
}
