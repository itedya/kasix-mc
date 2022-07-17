package com.itedya.skymaster.runnables.block;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;

public class BlockPlayerFromVisitIslandRunnable extends BukkitRunnable {
    private Connection connection;
    private final Player executor;
    private final Player userToBlock;
    public BlockPlayerFromVisitIslandRunnable(Player executor, Player userToBlock){
        this.executor = executor;
        this.userToBlock = userToBlock;
    }
    @Override
    public void run() {

    }
}
