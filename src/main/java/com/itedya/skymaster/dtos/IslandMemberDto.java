package com.itedya.skymaster.dtos;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IslandMemberDto implements DatabaseDto {
    public static IslandMemberDto fromResultSet(ResultSet rs) throws SQLException {
        IslandMemberDto dto = new IslandMemberDto();
        dto.playerUuid = rs.getString("playerUuid");
        dto.islandId = rs.getInt("islandId");
        dto.createdAt = rs.getDate("createdAt");
        dto.updatedAt = rs.getDate("updatedAt");
        dto.deletedAt = rs.getDate("deletedAt");
        return dto;
    }

    public String playerUuid;
    public Integer islandId;
    public Date createdAt;
    public Date updatedAt;
    public Date deletedAt;
}
