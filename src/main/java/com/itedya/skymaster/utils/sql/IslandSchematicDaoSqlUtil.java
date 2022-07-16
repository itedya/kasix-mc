package com.itedya.skymaster.utils.sql;

public class IslandSchematicDaoSqlUtil {
    public static final String NAMED_COLUMNS = """
            id          as schematic_id,
            skymaster_schematics.name        as schematic_name,
            skymaster_schematics.description as schematic_description,
            skymaster_schematics.filePath    as schematic_filePath,
            skymaster_schematics.material    as schematic_material,
            skymaster_schematics.updatedAt   as schematic_updatedAt,
            skymaster_schematics.createdAt   as schematic_createdAt,
            skymaster_schematics.deletedAt   as schematic_deletedAt""";

    public static final String GET_BY_NAME_WITH_DELETED = "SELECT * FROM `skymaster_schematics` WHERE name = ?";
    public static final String GET_BY_NAME = GET_BY_NAME_WITH_DELETED + " AND deletedAt IS NULL";
    public static final String GET_ALL_WITH_DELETED = "SELECT * FROM `skymaster_schematics`";
    public static final String GET_ALL = GET_ALL_WITH_DELETED + " AND deletedAt IS NULL";
    public static final String GET_BY_ID_WITH_DELETED = "SELECT * FROM `skymaster_schematics` WHERE id = ?";
    public static final String GET_BY_ID = GET_BY_ID_WITH_DELETED + " AND deletedAt IS NULL";
    public static final String CREATE = "INSERT INTO `skymaster_schematics` SET `name` = ?, `description` = ?, `filePath` = ?, `material` = ?";
}
