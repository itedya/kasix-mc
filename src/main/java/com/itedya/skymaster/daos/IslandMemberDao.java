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
        statement.setInt(1, islandId);

        ResultSet rs = statement.executeQuery();

        List<IslandMemberDto> result = new ArrayList<>();

        while (rs.next()) {
            result.add(new IslandMemberDto(rs));
        }

        rs.close();
        statement.close();

        return result;
    }

    public int remove(String playerUuid, Integer islandId) throws SQLException {
        String query = "UPDATE `skymaster_island_has_members` SET deletedAt = CURRENT_TIMESTAMP WHERE playerUuid = ? AND islandId = ? AND deletedAt IS NULL";

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setString(1, playerUuid);
        stmt.setInt(2, islandId);

        int result = stmt.executeUpdate();
        stmt.close();

        return result;
    }

    public boolean isMember(String playerUuid, Integer islandId) throws SQLException {
        String query = "SELECT * FROM `skymaster_island_has_members` WHERE playerUuid = ? AND islandId = ? AND deletedAt IS NULL";

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setString(1, playerUuid);
        stmt.setInt(2, islandId);

        stmt.executeQuery();

        ResultSet rs = stmt.getResultSet();

        boolean isMember = rs.next();

        rs.close();
        stmt.close();

        return isMember;
    }

    public IslandMemberDto create(IslandMemberDto dto) throws SQLException {
        String query = "INSERT INTO `skymaster_island_has_members` SET islandId = ?, playerUuid = ?";

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, dto.getIslandId());
        stmt.setString(2, dto.getPlayerUuid());

        stmt.executeUpdate();
        stmt.close();

        return dto;
    }

}
