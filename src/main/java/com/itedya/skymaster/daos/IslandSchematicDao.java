package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.database.IslandSchematicDto;

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

    public IslandSchematicDto getByName(String data, Boolean withDeleted) throws SQLException {
        String query = "SELECT * FROM `skymaster_schematics` WHERE name = ?";

        if (!withDeleted) query += " AND deletedAt != null";

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, data);

        ResultSet rs = stmt.executeQuery();

        IslandSchematicDto schematic = null;
        if (rs.next()) schematic = new IslandSchematicDto(rs);

        rs.close();
        stmt.close();

        return schematic;
    }

    public List<IslandSchematicDto> getAll() throws SQLException {
        return getAll(false);
    }

    public List<IslandSchematicDto> getAll(Boolean withDeleted) throws SQLException {
        String query = "SELECT * FROM `skymaster_schematics`";

        if (!withDeleted) query += " WHERE deletedAt IS NULL";

        PreparedStatement stmt = connection.prepareStatement(query);

        ResultSet rs = stmt.executeQuery();

        List<IslandSchematicDto> result = new ArrayList<>();

        while (rs.next()) {
            var dto = new IslandSchematicDto(rs);
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
        String query = "SELECT * FROM `skymaster_schematics` WHERE id = ?";
        if (!withDeleted) query += " AND deletedAt IS NULL";

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        IslandSchematicDto result = null;

        if (rs.next()) {
            result = new IslandSchematicDto(rs);
        }

        rs.close();
        stmt.close();

        return result;
    }

    public void create(IslandSchematicDto islandSchematicDto) throws SQLException {
        String query = "INSERT INTO `skymaster_schematics` SET `name` = ?, `description` = ?, `filePath` = ?, `material` = ?";

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, islandSchematicDto.getName());
        stmt.setString(2, islandSchematicDto.getDescription());
        stmt.setString(3, islandSchematicDto.getFilePath());
        stmt.setString(4, islandSchematicDto.getMaterial().toString());

        stmt.executeUpdate();

        stmt.close();
    }
}
