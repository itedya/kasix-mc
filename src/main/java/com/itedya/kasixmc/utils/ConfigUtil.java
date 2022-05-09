package com.itedya.kasixmc.utils;

import com.itedya.kasixmc.KasixMC;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {
    public static FileConfiguration getConfig() {
        KasixMC plugin = KasixMC.getInstance();

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

    public static void createRequiredFiles() {
        KasixMC plugin = KasixMC.getInstance();

        String abs = plugin.getDataFolder().getAbsolutePath();

        plugin.saveDefaultConfig();

        new File(Path.of(abs, "schematics").toString()).mkdirs();
        plugin.saveResource("data/schematics.json", false);
        plugin.saveResource("data/islands.json", false);
    }
}
