package com.itedya.skymaster.utils;

import com.itedya.skymaster.SkyMaster;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtil {
    public static FileConfiguration getConfig() {
        SkyMaster plugin = SkyMaster.getInstance();

        return plugin.getConfig();
    }

    public static String getColouredString(String id) {
        FileConfiguration configuration = getConfig();
        String rawMessage = configuration.getString(id);

        if (rawMessage == null) return null;

        return ChatColor.translateAlternateColorCodes('&', rawMessage);
    }

    public static String getColouredString(String id, String defaultMessage) {
        String configMessage = getColouredString(id);
        if (configMessage == null) {
            return ChatColor.translateAlternateColorCodes('&', defaultMessage);
        }

        return configMessage;
    }
}
