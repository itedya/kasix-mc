package com.itedya.skymaster.dtos;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseDto {
    static DatabaseDto fromResultSet(ResultSet rs) throws SQLException {
        return null;
    }
}
