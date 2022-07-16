package com.itedya.skymaster.dtos.database;

import org.bukkit.Material;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IslandSchematicDto implements DatabaseDto {
    public int id;
    public String name;
    public String description;
    public String filePath;
    public Material material;
    public Date updatedAt;
    public Date createdAt;
    public Date deletedAt;

    public static IslandSchematicDto fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs);
    }

    public static IslandSchematicDto fromResultSet(ResultSet rs, String prefix) throws SQLException {
        IslandSchematicDto dto = new IslandSchematicDto();
        dto.id = rs.getInt(prefix + "id");
        dto.name = rs.getString(prefix + "name");
        dto.description = rs.getString(prefix + "description");
        dto.filePath = rs.getString(prefix + "filePath");
        dto.material = Material.valueOf(rs.getString(prefix + "material"));
        dto.updatedAt = rs.getDate(prefix + "updatedAt");
        dto.createdAt = rs.getDate(prefix + "createdAt");
        dto.deletedAt = rs.getDate(prefix + "deletedAt");
        return dto;
    }
}
