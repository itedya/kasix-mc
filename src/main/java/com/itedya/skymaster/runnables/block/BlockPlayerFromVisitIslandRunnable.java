package com.itedya.skymaster.runnables.block;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.daos.VisitBlockDao;
import com.itedya.skymaster.dtos.database.VisitBlockDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.itedya.skymaster.runnables.kick.KickPlayerFromIslandRunnable;

import java.sql.Connection;

public class BlockPlayerFromVisitIslandRunnable extends SkymasterRunnable {
    private final OfflinePlayer userToBlock;
    private final Player islandOwner;

    public BlockPlayerFromVisitIslandRunnable(Player executor, OfflinePlayer userToBlock) {
        super(executor, false);
        this.islandOwner = executor;
        this.userToBlock = userToBlock;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();
            var dao = new IslandDao(connection);
            var memberDao = new IslandMemberDao(connection);
            String uuid = userToBlock.getUniqueId().toString();
            var rawIslands = dao.getByOwnerUuidWithAllRelations(uuid);
            //  Get all islands -> For each island: if blocked user is islands contributor - KickPlayerFromIsland
            for (var island : rawIslands) {
                if (memberDao.isMember(uuid, island.id))
                    ThreadUtil.async(new KickPlayerFromIslandRunnable(islandOwner, island.id, uuid));
            }
            ThreadUtil.async(this::blockPlayerFromVisitIsland);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void blockPlayerFromVisitIsland() {
        try {
            //set block to DB, new checker in VisitIslandSubCommand
            Connection connection = Database.getInstance().getConnection();
            VisitBlockDao visitBlockDao = new VisitBlockDao(connection);
            var visitBlockDto = visitBlockDao.get(islandOwner.getUniqueId().toString(), userToBlock.getUniqueId().toString());
            if (visitBlockDto != null) {
                islandOwner.sendMessage(ChatColor.YELLOW + userToBlock.getName() + " jest już zablokowany");
            } else {
                visitBlockDto = new VisitBlockDto();
                visitBlockDto.blockedPlayerUuid = userToBlock.getUniqueId().toString();
                visitBlockDto.islandOwnerUuid = islandOwner.getUniqueId().toString();
                visitBlockDao.create(visitBlockDto);
                connection.commit();
                islandOwner.sendMessage(ChatColor.GREEN + "Pomyślnie zablokowano użytkownika " + userToBlock.getName());
            }
            connection.close();

        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
