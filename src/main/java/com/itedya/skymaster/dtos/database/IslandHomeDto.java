package com.itedya.skymaster.dtos.database;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IslandHomeDto implements DatabaseDto {
    public static IslandHomeDto fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs);
    }
        public static IslandHomeDto fromResultSet(ResultSet rs, String prefix) throws SQLException {
        IslandHomeDto islandHomeDto = new IslandHomeDto();
        islandHomeDto.id = rs.getInt(prefix + "id");
        islandHomeDto.x = rs.getInt(prefix + "x");
        islandHomeDto.y = rs.getInt(prefix + "y");
        islandHomeDto.z = rs.getInt(prefix + "z");
        islandHomeDto.worldUuid = rs.getString(prefix + "worldUuid");
        islandHomeDto.createdAt = rs.getDate(prefix + "createdAt");
        islandHomeDto.updatedAt = rs.getDate(prefix + "updatedAt");
        islandHomeDto.deletedAt = rs.getDate(prefix + "deletedAt");
        return islandHomeDto;
    }

    public int id;
    public int x;
    public int y;
    public int z;
    public String worldUuid;
    public Date createdAt;
    public Date updatedAt;
    public Date deletedAt;
}
