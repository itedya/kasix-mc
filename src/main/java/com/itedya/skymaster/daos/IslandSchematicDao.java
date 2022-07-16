package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.database.IslandSchematicDto;
import com.itedya.skymaster.utils.sql.IslandSchematicDaoSqlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IslandSchematicDao {
    private final Connection connection;

    public IslandSchematicDao(Connection connection) {
        this.connection = connection;
    }

    public IslandSchematicDto getByName(String data) throws SQLException {
        return getByName(data, false);
    }

    public IslandSchematicDto getByName(String name, Boolean withDeleted) throws SQLException {
        String query;
        if (withDeleted) {
            query = IslandSchematicDaoSqlUtil.GET_BY_NAME_WITH_DELETED;
        } else {
            query = IslandSchematicDaoSqlUtil.GET_BY_NAME;
        }

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, name);

        ResultSet rs = stmt.executeQuery();

        IslandSchematicDto schematic = null;
        if (rs.next()) schematic = IslandSchematicDto.fromResultSet(rs);

        rs.close();
        stmt.close();

        return schematic;
    }

    public List<IslandSchematicDto> getAll() throws SQLException {
        return getAll(false);
    }

    public List<IslandSchematicDto> getAll(Boolean withDeleted) throws SQLException {
        String query;
        if (withDeleted) {
            query = IslandSchematicDaoSqlUtil.GET_BY_NAME_WITH_DELETED;
        } else {
            query = IslandSchematicDaoSqlUtil.GET_BY_NAME;
        }

        PreparedStatement stmt = connection.prepareStatement(query);

        ResultSet rs = stmt.executeQuery();

        List<IslandSchematicDto> result = new ArrayList<>();

        while (rs.next()) {
            var dto = IslandSchematicDto.fromResultSet(rs);
            result.add(dto);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public IslandSchematicDto getById(int id) throws SQLException {
        return getById(id, false);
    }

    public IslandSchematicDto getById(int id, Boolean withDeleted) throws SQLException {
        String query;
        if (withDeleted) {
            query = IslandSchematicDaoSqlUtil.GET_BY_ID_WITH_DELETED;
        } else {
            query = IslandSchematicDaoSqlUtil.GET_BY_ID;
        }

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        IslandSchematicDto result = null;

        if (rs.next()) {
            result = IslandSchematicDto.fromResultSet(rs);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public void create(IslandSchematicDto islandSchematicDto) throws SQLException {
        String query = IslandSchematicDaoSqlUtil.CREATE;

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, islandSchematicDto.name);
        stmt.setString(2, islandSchematicDto.description);
        stmt.setString(3, islandSchematicDto.filePath);
        stmt.setString(4, islandSchematicDto.material.toString());

        stmt.executeUpdate();

        stmt.close();
    }
}
