package com.itedya.skymaster.dtos;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IslandHomeDto implements DatabaseDto {
    public static IslandHomeDto fromResultSet(ResultSet rs) throws SQLException {
        IslandHomeDto islandHomeDto = new IslandHomeDto();
        islandHomeDto.id = rs.getInt("id");
        islandHomeDto.x = rs.getInt("x");
        islandHomeDto.y = rs.getInt("y");
        islandHomeDto.z = rs.getInt("z");
        islandHomeDto.worldUuid = rs.getString("worldUuid");
        islandHomeDto.createdAt = rs.getDate("createdAt");
        islandHomeDto.updatedAt = rs.getDate("updatedAt");
        islandHomeDto.deletedAt = rs.getDate("deletedAt");
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
