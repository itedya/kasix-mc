package com.itedya.skymaster.runnables.block;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.VisitBlockDao;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;

public class UnblockPlayerFromVisitIslandRunnable extends SkymasterRunnable {
    private final OfflinePlayer userToUnblock;
    private final Player islandOwner;

    public UnblockPlayerFromVisitIslandRunnable(Player executor, OfflinePlayer userToUnblock) {
        super(executor, false);
        this.islandOwner = executor;
        this.userToUnblock = userToUnblock;
    }

    @Override
    public void run() {
        try {
            ThreadUtil.async(this::unblockPlayerFromVisitIsland);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void unblockPlayerFromVisitIsland() {
        try {
            this.connection = Database.getInstance().getConnection();
            VisitBlockDao visitBlockDao = new VisitBlockDao(connection);

            var visitBlockDto = visitBlockDao.get(islandOwner.getUniqueId().toString(), userToUnblock.getUniqueId().toString());

            if (visitBlockDto != null) {
                visitBlockDao.delete(islandOwner.getUniqueId().toString(), userToUnblock.getUniqueId().toString());
                connection.commit();
                executor.sendRawMessage(ChatColor.GREEN + "Użytkownik " + userToUnblock.getName() + " został odblokowany!");
            } else {
                executor.sendRawMessage(ChatColor.YELLOW + "Użytkownik " + userToUnblock.getName() + " nie jest zablokowany!");
            }

            connection.close();
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
