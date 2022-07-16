package com.itedya.skymaster.dtos.database;

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
        return fromResultSet(resultSet, "");
    }

    public static IslandDto fromResultSet(ResultSet resultSet, String prefix) throws SQLException {
        IslandDto islandDto = new IslandDto();
        islandDto.id = resultSet.getInt(prefix + "id");
        islandDto.name = resultSet.getString(prefix + "name");
        islandDto.ownerUuid = resultSet.getString(prefix + "ownerUuid");
        islandDto.radius = resultSet.getInt(prefix + "radius");
        islandDto.schematicId = resultSet.getInt(prefix + "schematicId");
        islandDto.createdAt = resultSet.getDate(prefix + "createdAt");
        islandDto.updatedAt = resultSet.getDate(prefix + "updatedAt");
        islandDto.deletedAt = resultSet.getDate(prefix + "deletedAt");
        return islandDto;
    }
}