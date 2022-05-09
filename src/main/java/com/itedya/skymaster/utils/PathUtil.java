package com.itedya.skymaster.utils;

import com.itedya.skymaster.SkyMaster;

import java.nio.file.Path;

public class PathUtil {
    public static String getDataFilePath(String filePath) {
        SkyMaster plugin = SkyMaster.getInstance();

        Path path = Path.of(plugin.getDataFolder().toString(), filePath);

        return path.toString();
    }

    public static String getSchematicFilePath(String fileName) {
        SkyMaster plugin = SkyMaster.getInstance();

        Path path = Path.of(plugin.getDataFolder().toString(), "schematics", fileName);

        return path.toString();
    }
}
