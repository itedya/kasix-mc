package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.database.IslandHomeDto;
import com.itedya.skymaster.utils.sql.IslandHomeDaoSqlUtil;

import java.sql.*;

public class IslandHomeDao {
    private final Connection connection;

    public IslandHomeDao(Connection connection) {
        this.connection = connection;
    }

    public IslandHomeDto getByIslandId(int id) throws SQLException {
        return getByIslandId(id, false);
    }

    public IslandHomeDto getByIslandId(int id, Boolean withDeleted) throws SQLException {
        String query;
        if (withDeleted) {
            query = IslandHomeDaoSqlUtil.GET_BY_ISLAND_ID_WITH_DELETED;
        } else {
            query = IslandHomeDaoSqlUtil.GET_BY_ISLAND_ID;
        }

        IslandHomeDto result = null;
        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) result = IslandHomeDto.fromResultSet(rs);

        rs.close();
        stmt.close();

        return result;
    }

    public IslandHomeDto create(int islandId, IslandHomeDto islandHomeDto) throws SQLException {
        String query = IslandHomeDaoSqlUtil.CREATE;
        String relationQuery = IslandHomeDaoSqlUtil.CREATE_RELATION_QUERY;

        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        stmt.setInt(1, islandHomeDto.x);
        stmt.setInt(2, islandHomeDto.y);
        stmt.setInt(3, islandHomeDto.z);
        stmt.setString(4, islandHomeDto.worldUuid);

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) throw new SQLException("No rows affected!");

        ResultSet generatedKeys = stmt.getGeneratedKeys();

        if (generatedKeys.next()) islandHomeDto.id = generatedKeys.getInt(1);
        else throw new SQLException("No id generated for added home!");

        generatedKeys.close();
        stmt.close();

        stmt = connection.prepareStatement(relationQuery);

        stmt.setInt(1, islandId);
        stmt.setInt(2, islandHomeDto.id);

        stmt.executeUpdate();

        stmt.close();

        return islandHomeDto;
    }

    public void updateByIslandId(int islandId, IslandHomeDto islandHomeDto) throws SQLException {
        String query = IslandHomeDaoSqlUtil.UPDATE_BY_ISLAND_ID;

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, islandHomeDto.x);
        stmt.setInt(2, islandHomeDto.y);
        stmt.setInt(3, islandHomeDto.z);
        stmt.setString(4, islandHomeDto.worldUuid);
        stmt.setInt(5, islandId);

        stmt.executeUpdate();

        stmt.close();
    }

    public void deleteById(int id) throws SQLException {
        String query = IslandHomeDaoSqlUtil.DELETE_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, id);

        stmt.executeUpdate();

        stmt.close();
    }
}
