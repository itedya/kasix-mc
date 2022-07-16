package com.itedya.skymaster.utils.sql;

public class IslandDaoSqlUtil {
    public static final String NAMED_COLUMNS = "" +
            "skymaster_islands.id as island_id,\n" +
            "skymaster_islands.name as island_name,\n" +
            "skymaster_islands.ownerUuid as island_ownerUuid,\n" +
            "skymaster_islands.schematicId as island_schematicId,\n" +
            "skymaster_islands.radius as island_radius,\n" +
            "skymaster_islands.updatedAt as island_updatedAt,\n" +
            "skymaster_islands.createdAt as island_createdAt,\n" +
            "skymaster_islands.deletedAt as island_deletedAt";

    public static final String GET_SUM_BY_OWNER_UUID = "SELECT count(*) FROM `skymaster_islands` WHERE `ownerUuid` = ? AND `deletedAt` IS NULL";
    public static final String GET_SUM_BY_OWNER_UUID_WITH_DELETED = "SELECT count(*) FROM `skymaster_islands` WHERE `ownerUuid` = ?";
    public static final String GET_BY_OWNER_UUID_WITH_DELETED = "SELECT * FROM `skymaster_islands` WHERE `ownerUuid` = ?";
    public static final String GET_BY_OWNER_UUID = "SELECT * FROM `skymaster_islands` WHERE `ownerUuid` = ? AND deletedAt IS NULL";
    public static final String GET_COUNT_WITH_DELETED = "SELECT count(*) as `size` FROM skymaster_islands";
    public static final String GET_COUNT = "SELECT count(*) as `size` FROM skymaster_islands WHERE deletedAt IS NULL";
    public static final String CREATE = "INSERT INTO `skymaster_islands` SET ownerUuid = ?, schematicId = ?, name = ?, radius = ?";
    public static final String GET_BY_ID_WITH_DELETED = "SELECT * FROM `skymaster_islands` WHERE `id` = ?";
    public static final String GET_BY_ID = "SELECT * FROM `skymaster_islands` WHERE `id` = ? AND deletedAt IS NULL";
    public static final String UPDATE = "UPDATE `skymaster_islands` SET ownerUuid = ?, schematicId = ?, name = ?, radius = ? WHERE id = ?";
    public static final String REMOVE_BY_ID = "UPDATE `skymaster_islands` SET deletedAt = CURRENT_TIMESTAMP WHERE id = ?";
    public static final String GET_WITH_ALL_RELATIONS_BY_OWNER_UUID_WITH_DELETED = "" +
            "SELECT " +
            NAMED_COLUMNS + ", " +
            IslandHomeDaoSqlUtil.NAMED_COLUMNS + ", " +
            IslandSchematicDaoSqlUtil.NAMED_COLUMNS + " " +
            "FROM `skymaster_islands` " +
            "JOIN skymaster_island_has_homes ON skymaster_islands.id = skymaster_island_has_homes.islandId " +
            "JOIN skymaster_homes ON skymaster_homes.id = skymaster_island_has_homes.homeId " +
            "JOIN skymaster_schematics ON skymaster_schematics.id = skymaster_islands.schematicId " +
            "WHERE skymaster_islands.ownerUuid = ?";
    public static final String GET_WITH_ALL_RELATIONS_BY_OWNER_UUID =
            GET_WITH_ALL_RELATIONS_BY_OWNER_UUID_WITH_DELETED + " AND skymaster_islands.deletedAt IS NULL";

    public static final String GET_WITH_ALL_RELATIONS_BY_MEMBER_UUID_WITH_DELETED = "" +
            "SELECT " +
            NAMED_COLUMNS + ", " +
            IslandHomeDaoSqlUtil.NAMED_COLUMNS + ", " +
            IslandSchematicDaoSqlUtil.NAMED_COLUMNS + " " +
            "FROM `skymaster_island_has_members` " +
            "JOIN skymaster_islands ON skymaster_island_has_members.islandId = skymaster_islands.id " +
            "JOIN skymaster_island_has_homes ON skymaster_islands.id = skymaster_island_has_homes.islandId " +
            "JOIN skymaster_homes ON skymaster_homes.id = skymaster_island_has_homes.homeId " +
            "JOIN skymaster_schematics ON skymaster_schematics.id = skymaster_islands.schematicId " +
            "WHERE skymaster_island_has_members.playerUuid = ?";

    public static final String GET_WITH_ALL_RELATIONS_BY_MEMBER_UUID =
            GET_WITH_ALL_RELATIONS_BY_MEMBER_UUID_WITH_DELETED + " AND skymaster_islands.deletedAt IS NULL";

    public static final String GET_FIRST_NTH_BY_ISLAND_RADIUS_WITH_DELETED = "SELECT * FROM `skymaster_islands` ORDER BY `radius` DESC";
    public static final String GET_FIRST_NTH_BY_ISLAND_RADIUS = "SELECT * FROM `skymaster_islands` WHERE `deletedAt` IS NULL ORDER BY `radius` DESC";
}
