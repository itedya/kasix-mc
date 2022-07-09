package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.ViewBlockDto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewBlockDao {
    private final Connection connection;

    ViewBlockDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets ViewBlockDto with given islandId and blocked player uuid
     *
     * @param islandId          Island ID
     * @param blockedPlayerUuid Blocked Player UUID (String)
     * @return ViewBlockDto
     */
    public ViewBlockDto get(int islandId, String blockedPlayerUuid) throws SQLException {
        return get(islandId, blockedPlayerUuid, false);
    }

    /**
     * Gets ViewBlockDto with given islandId and blocked player uuid
     *
     * @param islandId          Island ID
     * @param blockedPlayerUuid Blocked Player UUID (String)
     * @param withDeleted       Switch if search for deleted entries
     * @return ViewBlockDto
     */
    public ViewBlockDto get(int islandId, String blockedPlayerUuid, Boolean withDeleted) throws SQLException {
        String query = "SELECT * FROM skymaster_view_blocks WHERE islandId = ? AND blockedPlayerUuid = ?";
        if (!withDeleted) {
            query += " AND deletedAt IS NULL";
        }

        var stmt = connection.prepareStatement(query);
        stmt.setInt(1, islandId);
        stmt.setString(2, blockedPlayerUuid);

        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        var dto = ViewBlockDto.fromResultSet(rs);

        rs.close();
        stmt.close();

        return dto;
    }
}
