package com.itedya.skymaster.runnables.block;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.VisitBlockDao;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;

public class UnblockPlayerFromVisitIslandRunnable extends BukkitRunnable {
    private final Player executor;
    private final OfflinePlayer userToUnblock;
    Connection connection;
    public UnblockPlayerFromVisitIslandRunnable(Player executor, OfflinePlayer userToUnblock){
        this.executor = executor;
        this.userToUnblock = userToUnblock;
    }
    @Override
    public void run() {
        try{
            ThreadUtil.async(this::unblockPlayerFromVisitIsland);
        }catch(Exception e){

        }
    }

    public void unblockPlayerFromVisitIsland(){
        try{
            //set block to DB, new checker in VisitIslandSubCommand
            this.connection = Database.getInstance().getConnection();
            VisitBlockDao visitBlockDao = new VisitBlockDao(connection);
            var visitBlockDto = visitBlockDao.get(executor.getUniqueId().toString(), userToUnblock.getUniqueId().toString());
            if(visitBlockDto != null)
                visitBlockDao.delete(executor.getUniqueId().toString(), userToUnblock.getUniqueId().toString());
            connection.close();

        }catch(Exception e){
            ThreadUtil.async(this::shutdown);
            e.printStackTrace();
            executor.sendMessage(ChatUtil.SERVER_ERROR);
        }
    }

    public void shutdown() {
        try {
            if (this.connection != null) {
                this.connection.rollback();
                this.connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
