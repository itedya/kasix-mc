package com.itedya.skymaster.utils;

import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;

import java.sql.Connection;
import java.sql.SQLException;

public class IslandUtil {
    public static double getExpandCost(int radius) {
        return Math.pow(radius * 2, 2);
    }

    public static int getIslandAmount(Connection connection, String playerUuid) throws SQLException {
        IslandDao islandDao = new IslandDao(connection);
        IslandMemberDao islandMemberDao = new IslandMemberDao(connection);
        int ownerSum = islandDao.getSumByOwnerUuid(playerUuid);
        int memberSum = islandMemberDao.getSumByMemberUuid(playerUuid);

        return ownerSum + memberSum;
    }
}
