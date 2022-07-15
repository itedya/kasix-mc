package com.itedya.skymaster.dtos;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class IslandDto implements DatabaseDto {
    public int id;
    public String name;
    public String ownerUuid;
    public int schematicId;
    public int radius;
    public Date updatedAt;
    public Date createdAt;
    public Date deletedAt;
    public IslandHomeDto home;
    public IslandSchematicDto schematic;
    public List<IslandMemberDto> members;

    public IslandDto() {
    }

    public static IslandDto fromResultSet(ResultSet resultSet) throws SQLException {
        IslandDto islandDto = new IslandDto();
        islandDto.id = resultSet.getInt("id");
        islandDto.name = resultSet.getString("name");
        islandDto.ownerUuid = resultSet.getString("ownerUuid");
        islandDto.radius = resultSet.getInt("radius");
        islandDto.schematicId = resultSet.getInt("schematicId");
        islandDto.createdAt = resultSet.getDate("createdAt");
        islandDto.updatedAt = resultSet.getDate("updatedAt");
        islandDto.deletedAt = resultSet.getDate("deletedAt");
        return islandDto;
    }
}