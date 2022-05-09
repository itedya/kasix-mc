package com.itedya.skymaster;

import com.itedya.skymaster.command.IslandCommand;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.listeners.CreateIslandGUIHandler;
import com.itedya.skymaster.listeners.IslandInfoGUIHandler;
import com.itedya.skymaster.listeners.ListUserIslandsGUIHandler;
import com.itedya.skymaster.utils.CommandUtil;
import com.itedya.skymaster.utils.ConfigUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class SkyMaster extends JavaPlugin {
    private static SkyMaster instance;
    private static Logger logger;

    public static Logger getPluginLogger() {
        return logger;
    }

    public static SkyMaster getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = this.getLogger();

        this.saveDefaultConfig();

        CommandUtil.registerCommand(new IslandCommand("wyspa"));

        if (!WorldUtil.doesWorldExists("world_islands")) {
            WorldUtil.createVoidWorld("world_islands");
        }

        ConfigUtil.createRequiredFiles();

        getServer().getPluginManager().registerEvents(new CreateIslandGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new ListUserIslandsGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new IslandInfoGUIHandler(), this);

        ThreadUtil.asyncRepeat(() -> {
            try {
                IslandSchematicDao islandSchematicDao = IslandSchematicDao.getInstance();
                IslandDao islandDao = IslandDao.getInstance();
                islandSchematicDao.saveToFile();
                islandDao.saveToFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 600);
    }

    @Override
    public void onDisable() {
        try {
            IslandSchematicDao islandSchematicDao = IslandSchematicDao.getInstance();
            IslandDao islandDao = IslandDao.getInstance();
            islandSchematicDao.saveToFile();
            islandDao.saveToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
