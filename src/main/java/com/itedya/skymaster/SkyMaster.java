package com.itedya.skymaster;

import com.itedya.skymaster.command.IslandCommand;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.guihandlers.*;
import com.itedya.skymaster.rankings.IslandSizeRankingManager;
import com.itedya.skymaster.utils.WorldUtil;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

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

        if (!setupEconomy()) {
            this.getLogger().severe("Couldn't set up VaultAPI! Plugin won't function normally.");
        }

        IslandSizeRankingManager.getInstance();

        this.saveDefaultConfig();
        Database.getInstance().migrate();

        IslandCommand.register();

        if (!WorldUtil.doesWorldExists("world_islands")) {
            WorldUtil.createVoidWorld("world_islands");
        }

        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        new SkyMasterPlaceholderExpansion().register();
    }

    private static Economy econ = null;

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
