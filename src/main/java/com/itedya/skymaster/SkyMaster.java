package com.itedya.skymaster;

import com.itedya.skymaster.command.IslandCommand;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.guihandler.ChooseMemberToKickGUIHandler;
import com.itedya.skymaster.guihandler.VisitIslandGUIHandler;
import com.itedya.skymaster.listeners.*;
import com.itedya.skymaster.rankings.IslandSizeRankingManager;
import com.itedya.skymaster.utils.CommandUtil;
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

        CommandUtil.registerCommand(new IslandCommand("wyspa"));

        if (!WorldUtil.doesWorldExists("world_islands")) {
            WorldUtil.createVoidWorld("world_islands");
        }

        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new CreateIslandGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new ListUserIslandsGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new IslandInfoGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new ChooseIslandInviteMemberGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new ChooseIslandToKickFromGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new ChooseMemberToKickGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new VisitIslandGUIHandler(), this);

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
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
