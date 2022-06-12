package com.itedya.skymaster.daos;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.dtos.IslandSchematicDto;
import com.itedya.skymaster.exceptions.ServerError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class IslandSchematicDao {
    private final Connection connection;

    public IslandSchematicDao(Connection connection) {
        this.connection = connection;
    }

    public IslandSchematicDto getByName(String data) throws ServerError {
        return getByName(data, false);
    }

    public IslandSchematicDto getByName(String data, Boolean withDeleted) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "SELECT * FROM `skymaster_schematics` WHERE name = ?";

        if (!withDeleted) query += " AND deletedAt != null";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, data);

            ResultSet rs = stmt.executeQuery();

            connection.close();

            stmt.close();

            if (rs.next()) {
                rs.close();
                return new IslandSchematicDto(rs);
            }

            rs.close();

            return null;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public List<IslandSchematicDto> getAll() throws ServerError {
        return getAll(false);
    }

    public List<IslandSchematicDto> getAll(Boolean withDeleted) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "SELECT * FROM `skymaster_schematics`";

        if (!withDeleted) query += " WHERE deletedAt IS NULL";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();

            List<IslandSchematicDto> result = new ArrayList<>();

            while (rs.next()) {
                IslandSchematicDto dto = new IslandSchematicDto(rs);
                result.add(dto);
            }

            rs.close();
            stmt.close();

            return result;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public IslandSchematicDto getById(int id) throws ServerError {
        return getById(id, false);
    }

    public IslandSchematicDto getById(int id, Boolean withDeleted) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();
        String query = "SELECT * FROM `skymaster_schematics` WHERE id = ?";
        if (!withDeleted) query += " AND deletedAt IS NULL";

        try {
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
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public void create(IslandSchematicDto islandSchematicDto) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();
        String query = "INSERT INTO `skymaster_schematics` SET `name` = ?, `description` = ?, `filePath` = ?, `material` = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, islandSchematicDto.getName());
            stmt.setString(2, islandSchematicDto.getDescription());
            stmt.setString(3, islandSchematicDto.getFilePath());
            stmt.setString(4, islandSchematicDto.getMaterial().toString());

            stmt.executeUpdate();

            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }
}
