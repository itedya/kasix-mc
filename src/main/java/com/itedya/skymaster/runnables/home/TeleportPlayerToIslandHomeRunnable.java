package com.itedya.skymaster.runnables.home;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandHomeDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.utils.IslandHomeUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;

public class TeleportPlayerToIslandHomeRunnable extends BukkitRunnable {
    private final Player player;
    private final Integer islandId;
    private Connection connection;

    public TeleportPlayerToIslandHomeRunnable(Player player, Integer islandId) {
        this.player = player;
        this.islandId = islandId;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();
            IslandHomeDao islandHomeDao = new IslandHomeDao(connection);
            IslandHomeDto islandHomeDto = islandHomeDao.firstByIslandId(islandId);

            ThreadUtil.sync(() -> IslandHomeUtil.addPlayerToQueue(player, islandHomeDto));
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");

            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
