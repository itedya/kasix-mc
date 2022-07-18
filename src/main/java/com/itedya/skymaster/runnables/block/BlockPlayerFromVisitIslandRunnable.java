package com.itedya.skymaster.runnables.block;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.daos.VisitBlockDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.itedya.skymaster.runnables.kick.KickPlayerFromIslandRunnable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockPlayerFromVisitIslandRunnable extends BukkitRunnable {
    private final Player executor;
    private final OfflinePlayer userToBlock;
    private Connection connection;
    public BlockPlayerFromVisitIslandRunnable(Player executor, OfflinePlayer userToBlock){
        this.executor = executor;
        this.userToBlock = userToBlock;
    }
    @Override
    public void run() {
        try{
            this.connection = Database.getInstance().getConnection();
            var dao = new IslandDao(connection);
            var memberDao = new IslandMemberDao(connection);
            String uuid = userToBlock.getUniqueId().toString();
            var rawIslands = dao.getByOwnerUuidWithAllRelations(uuid);
            //  Get all islands -> For each island: if blocked user is islands contributor - KickPlayerFromIsland
            for (var island : rawIslands) {
                if(memberDao.isMember(uuid, island.id))
                    ThreadUtil.async(new KickPlayerFromIslandRunnable(executor, island.id, uuid));
            }
            ThreadUtil.async(this::blockPlayerFromVisitIsland);
        }catch(Exception e){

        }
    }

    public void blockPlayerFromVisitIsland(){
        try{
            //set block to DB, new checker in VisitIslandSubCommand
            Connection connection = Database.getInstance().getConnection();
            VisitBlockDao visitBlockDao = new VisitBlockDao(connection);
            var visitBlockDto = visitBlockDao.get(executor.getUniqueId().toString(),userToBlock.getUniqueId().toString());
            if(visitBlockDto != null)
                executor.sendMessage(org.bukkit.ChatColor.YELLOW + userToBlock.getName() + " jest już zablokowany");
            else
                visitBlockDao.create(visitBlockDto);
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
