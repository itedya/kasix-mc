package com.itedya.skymaster.runnables;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandMemberDto;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.List;

public class ResetWorldGuardPermissionsRunnable extends BukkitRunnable {
    private final Integer islandId;
    private final Player logToPlayer;
    private IslandDto islandDto;
    private List<IslandMemberDto> members;
    private Connection connection;

    public ResetWorldGuardPermissionsRunnable(Player logToPlayer, Integer islandId) {
        this.islandId = islandId;
        this.logToPlayer = logToPlayer;
    }

    @Override
    public void run() {
        try {
            // it would be better if it completely removed region and recreated it
            connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);
            IslandMemberDao islandMemberDao = new IslandMemberDao(connection);

            this.islandDto = islandDao.getById(islandId);
            this.members = islandMemberDao.getByIslandId(this.islandId);

            connection.close();

            ThreadUtil.sync(this::resetWorldGuard);
        } catch (Exception e) {
            try {
                connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            logToPlayer.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
        }
    }

    public void resetWorldGuard() {
        ProtectedRegion protectedRegion = WorldGuardUtil.getRegionForId(islandId);
        WorldGuardUtil.resetRegionFlags(protectedRegion);
        WorldGuardUtil.resetRegionMembers(protectedRegion, islandDto, members);
        WorldGuardUtil.resetPriority(protectedRegion);

        logToPlayer.sendMessage(ChatColor.GREEN + "Zresetowano ustawienia WorldGuard dla tej wyspy.");
    }
}
