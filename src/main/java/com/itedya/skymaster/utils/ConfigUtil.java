package com.itedya.skymaster.utils;

import com.itedya.skymaster.SkyMaster;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtil {
    public static FileConfiguration getConfig() {
        SkyMaster plugin = SkyMaster.getInstance();

        return plugin.getConfig();
    }
}
