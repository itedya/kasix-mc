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
}
