package com.itedya.skymaster.utils.sql;

public class IslandHomeDaoSqlUtil {
    public static final String NAMED_COLUMNS = """
            skymaster_homes.id as home_id,
            skymaster_homes.x as home_x,
            skymaster_homes.y as home_y,
            skymaster_homes.z as home_z,
            skymaster_homes.worldUuid as home_worldUuid,
            skymaster_homes.updatedAt as home_updatedAt,
            skymaster_homes.createdAt as home_createdAt,
            skymaster_homes.deletedAt as home_deletedAt""";

    public static final String GET_BY_ISLAND_ID_WITH_DELETED = """
            SELECT skymaster_homes.* FROM skymaster_islands
                JOIN skymaster_island_has_homes ON skymaster_islands.id = skymaster_island_has_homes.islandId
                JOIN skymaster_homes ON skymaster_island_has_homes.homeId = skymaster_homes.id
            WHERE skymaster_islands.id = ?""";

    public static final String GET_BY_ISLAND_ID = GET_BY_ISLAND_ID_WITH_DELETED + " AND skymaster_island_has_homes.deletedAt IS NULL";
    public static final String CREATE = "INSERT INTO `skymaster_homes` SET x = ?, y = ?, z = ?, worldUuid = ?";
    public static final String CREATE_RELATION_QUERY = "INSERT INTO `skymaster_island_has_homes` SET islandId = ?, homeId = ?";
    public static final String UPDATE_BY_ISLAND_ID = """
            UPDATE `skymaster_homes`
                JOIN `skymaster_island_has_homes` ON `skymaster_homes`.`id` = `skymaster_island_has_homes`.`homeId`
                JOIN `skymaster_islands` ON `skymaster_island_has_homes`.`islandId` = `skymaster_islands`.`id`
            SET x = ?, y = ?, z = ?, worldUuid = ? WHERE `skymaster_islands`.`id` = ?""";

    public static final String DELETE_BY_ID = "UPDATE `skymaster_homes` SET deletedAt = CURRENT_TIMESTAMP WHERE id = ?;";
}
