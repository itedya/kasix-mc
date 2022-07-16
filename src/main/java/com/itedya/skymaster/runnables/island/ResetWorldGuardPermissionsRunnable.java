package com.itedya.skymaster.runnables.island;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandMemberDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;

import java.util.List;

public class ResetWorldGuardPermissionsRunnable extends SkymasterRunnable {
    public ResetWorldGuardPermissionsRunnable(Conversable executor, Integer islandId) {
        super(executor, true);

        data.put("islandId", islandId);
        data.put("executor", executor);
    }

    @Override
    public void run() {
        try {
            connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);
            IslandMemberDao islandMemberDao = new IslandMemberDao(connection);

            var islandId = (int) data.get("islandId");

            var islandDto = islandDao.getById(islandId);
            var members = islandMemberDao.getByIslandId(islandId);

            data.put("islandDto", islandDto);
            data.put("members", members);

            connection.close();

            ThreadUtil.sync(this::resetWorldGuard);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void resetWorldGuard() {
        try {
            var islandDto = (IslandDto) data.get("islandDto");

            var manager = WorldGuardUtil.getRegionManager();

            ProtectedRegion protectedRegion = WorldGuardUtil.getRegionForId(islandDto.getId());
            if (protectedRegion != null) manager.removeRegion(protectedRegion.getId());

            var radius = islandDto.getRadius();

            var middleVector = WorldGuardUtil.calculateIslandPosition(islandDto.getId());

            protectedRegion = WorldGuardUtil.createRegionWithoutSaving(
                    "island_" + islandDto.getId(),
                    BlockVector3.at(middleVector.getX() - radius, -64, middleVector.getZ() + radius),
                    BlockVector3.at(middleVector.getX() + radius, 319, middleVector.getZ() - radius)
            );

            var members = (List<IslandMemberDto>) data.get("members");

            WorldGuardUtil.resetRegionFlags(protectedRegion);
            WorldGuardUtil.resetRegionMembers(protectedRegion, islandDto, members);
            WorldGuardUtil.resetPriority(protectedRegion);

            manager.addRegion(protectedRegion);

            var executor = (Conversable) data.get("executor");

            executor.sendRawMessage(ChatColor.GREEN + "Zresetowano ustawienia WorldGuard dla tej wyspy.");
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
