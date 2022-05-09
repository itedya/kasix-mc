package com.itedya.kasixmc.utils;

import com.itedya.kasixmc.KasixMC;

import java.nio.file.Path;

public class PathUtil {
    public static String getDataFilePath(String filePath) {
        KasixMC plugin = KasixMC.getInstance();

        Path path = Path.of(plugin.getDataFolder().toString(), filePath);

        return path.toString();
    }

    public static String getSchematicFilePath(String fileName) {
        KasixMC plugin = KasixMC.getInstance();

        Path path = Path.of(plugin.getDataFolder().toString(), "schematics", fileName);

        return path.toString();
    }
}
