package com.itedya.skymaster.utils.sql;

public class IslandMemberDaoSqlUtil {
    public static final String CREATE = "INSERT INTO `skymaster_island_has_members` SET islandId = ?, playerUuid = ?";
    public static final String IS_MEMBER = "SELECT * FROM `skymaster_island_has_members` WHERE playerUuid = ? AND islandId = ? AND deletedAt IS NULL";
    public static final String REMOVE = "UPDATE `skymaster_island_has_members` SET deletedAt = CURRENT_TIMESTAMP WHERE playerUuid = ? AND islandId = ? AND deletedAt IS NULL";
    public static final String GET_BY_ISLAND_ID_WITH_DELETED = "SELECT * FROM `skymaster_island_has_members` WHERE islandId = ?";
    public static final String GET_BY_ISLAND_ID = GET_BY_ISLAND_ID_WITH_DELETED + " AND deletedAt IS NULL";
    public static final String GET_SUM_BY_MEMBER_UUID_WITH_DELETED = "SELECT count(*) FROM `skymaster_island_has_members` WHERE playerUuid = ?";
    public static final String GET_SUM_BY_MEMBER_UUID = GET_SUM_BY_MEMBER_UUID_WITH_DELETED + " AND deletedAt IS NULL";
}
