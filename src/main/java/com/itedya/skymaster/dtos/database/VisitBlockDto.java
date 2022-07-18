package com.itedya.skymaster.dtos.database;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VisitBlockDto implements DatabaseDto {
    public int id;
    public String islandOwnerUuid;
    public String blockedPlayerUuid;
    public Date updatedAt;
    public Date createdAt;
    public Date deletedAt;

    public static VisitBlockDto fromResultSet(ResultSet rs) throws SQLException {
        var dto = new VisitBlockDto();
        dto.id = rs.getInt("id");
        dto.islandOwnerUuid = rs.getString("islandOwnerUuid");
        dto.blockedPlayerUuid = rs.getString("blockedPlayerUuid");
        dto.updatedAt = rs.getDate("updatedAt");
        dto.createdAt = rs.getDate("createdAt");
        dto.deletedAt = rs.getDate("deletedAt");
        return dto;
    }
}
