package com.itedya.skymaster.runnables.island;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;

public class RemoveIslandRunnable extends BukkitRunnable {
    private final Integer islandId;
    private final Player logPlayer;
    private Connection connection;

    public RemoveIslandRunnable(Player logPlayer, Integer islandId) {
        this.islandId = islandId;
        this.logPlayer = logPlayer;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);
            islandDao.removeById(islandId);

            WorldGuardUtil.removeRegionForId(islandId);

            this.connection.commit();
            this.connection.close();

            logPlayer.sendMessage(ChatColor.GREEN + "Usunięto wyspę.");
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            logPlayer.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
        }
    }
}
