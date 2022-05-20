package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.IslandMemberDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IslandMemberDao {
    private final Connection connection;

    public IslandMemberDao(Connection connection) {
        this.connection = connection;
    }

    public List<IslandMemberDto> getByIslandId(Integer islandId) throws SQLException {
        return getByIslandId(islandId, false);
    }

    public List<IslandMemberDto> getByIslandId(Integer islandId, Boolean withDeleted) throws SQLException {
        String query = "SELECT * FROM `skymaster_island_has_members` WHERE islandId = ?";
        if (!withDeleted) query += " AND deletedAt IS NULL";

        PreparedStatement statement = this.connection.prepareStatement(query);

        ResultSet rs = statement.executeQuery();

        List<IslandMemberDto> result = new ArrayList<>();

        while (rs.next()) {
            result.add(new IslandMemberDto(rs));
        }

        rs.close();
        statement.close();

        return result;
    }

}
