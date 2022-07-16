package com.itedya.skymaster.dtos.database;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewBlockDto implements DatabaseDto {
    public int id;
    public int islandId;
    public String blockedPlayerUuid;
    public Date updatedAt;
    public Date createdAt;
    public Date deletedAt;

    public static ViewBlockDto fromResultSet(ResultSet rs) throws SQLException {
        var dto = new ViewBlockDto();
        dto.id = rs.getInt("id");
        dto.islandId = rs.getInt("islandId");
        dto.blockedPlayerUuid = rs.getString("blockedPlayerUuid");
        dto.updatedAt = rs.getDate("updatedAt");
        dto.createdAt = rs.getDate("createdAt");
        dto.deletedAt = rs.getDate("deletedAt");
        return dto;
    }
}