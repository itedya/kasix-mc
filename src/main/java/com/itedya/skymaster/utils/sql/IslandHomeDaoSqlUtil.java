package com.itedya.skymaster.utils.sql;

public class IslandHomeDaoSqlUtil {
    public static final String NAMED_COLUMNS = """
            id as home_id,
            skymaster_homes.x as home_x,
            skymaster_homes.y as home_y,
            skymaster_homes.z as home_z,
            skymaster_homes.worldUuid as home_worldUuid,
            skymaster_homes.updatedAt as home_updatedAt,
            skymaster_homes.createdAt as home_createdAt,
            skymaster_homes.deletedAt as home_deletedAt""";
}
