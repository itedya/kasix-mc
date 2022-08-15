package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.database.IslandMemberDto;
import com.itedya.skymaster.utils.sql.IslandMemberDaoSqlUtil;

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
        String query;
        if (withDeleted) {
            query = IslandMemberDaoSqlUtil.GET_BY_ISLAND_ID_WITH_DELETED;
        } else {
            query = IslandMemberDaoSqlUtil.GET_BY_ISLAND_ID;
        }

        PreparedStatement statement = this.connection.prepareStatement(query);
        statement.setInt(1, islandId);

        ResultSet rs = statement.executeQuery();

        List<IslandMemberDto> result = new ArrayList<>();

        while (rs.next()) {
            result.add(IslandMemberDto.fromResultSet(rs));
        }

        rs.close();
        statement.close();

        return result;
    }

    public int remove(String playerUuid, Integer islandId) throws SQLException {
        String query = IslandMemberDaoSqlUtil.REMOVE;

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setString(1, playerUuid);
        stmt.setInt(2, islandId);

        int result = stmt.executeUpdate();
        stmt.close();

        return result;
    }

    public boolean isMember(String playerUuid, Integer islandId) throws SQLException {
        String query = IslandMemberDaoSqlUtil.IS_MEMBER;

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
        String query = IslandMemberDaoSqlUtil.CREATE;

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, dto.islandId);
        stmt.setString(2, dto.playerUuid);

        stmt.executeUpdate();
        stmt.close();

        return dto;
    }

    public int getSumByMemberUuid(String memberUuid) throws SQLException {
        return getSumByMemberUuid(memberUuid, false);
    }

    public int getSumByMemberUuid(String memberUuid, Boolean withDeleted) throws SQLException {
        String query = (withDeleted) ? IslandMemberDaoSqlUtil.GET_SUM_BY_MEMBER_UUID_WITH_DELETED : IslandMemberDaoSqlUtil.GET_SUM_BY_MEMBER_UUID;

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setString(1, memberUuid);

        ResultSet resultSet = stmt.executeQuery();

        Integer result = null;
        if (resultSet.next()) {
            result = resultSet.getInt("count(*)");
        } else {
            throw new SQLException("Result set is empty IslandDao:42");
        }

        resultSet.close();
        stmt.close();

        return result;
    }
}
