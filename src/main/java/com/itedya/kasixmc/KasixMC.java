package com.itedya.kasixmc;

import com.itedya.kasixmc.command.IslandCommand;
import com.itedya.kasixmc.daos.IslandDao;
import com.itedya.kasixmc.daos.IslandSchematicDao;
import com.itedya.kasixmc.listeners.CreateIslandGUIHandler;
import com.itedya.kasixmc.listeners.IslandInfoGUIHandler;
import com.itedya.kasixmc.listeners.ListUserIslandsGUIHandler;
import com.itedya.kasixmc.utils.CommandUtil;
import com.itedya.kasixmc.utils.ConfigUtil;
import com.itedya.kasixmc.utils.ThreadUtil;
import com.itedya.kasixmc.utils.WorldUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class KasixMC extends JavaPlugin {
    private static KasixMC instance;
    private static Logger logger;

    public static Logger getPluginLogger() {
        return logger;
    }

    public static KasixMC getInstance() {
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
