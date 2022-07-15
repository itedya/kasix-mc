package com.itedya.skymaster.dtos;

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
        IslandSchematicDto dto = new IslandSchematicDto();
        dto.id = rs.getInt("id");
        dto.name = rs.getString("name");
        dto.description = rs.getString("description");
        dto.filePath = rs.getString("filePath");
        dto.material = Material.valueOf(rs.getString("material"));
        dto.updatedAt = rs.getDate("updatedAt");
        dto.createdAt = rs.getDate("createdAt");
        dto.deletedAt = rs.getDate("deletedAt");
        return dto;
    }
}
