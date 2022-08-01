package com.itedya.skymaster.dtos.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IslandRateDto implements DatabaseDto{

    public int islandId;
    public String ratingPlayerUUID;
    public int value;

    public static IslandRateDto fromResultSet(ResultSet rs) throws SQLException {
        IslandRateDto dto = new IslandRateDto();
        dto.islandId = rs.getInt("ratingPlayerUUID");
        dto.ratingPlayerUUID = rs.getString("ratingPlayerUUID");
        dto.value = rs.getInt("value");
        return dto;
    }
}
