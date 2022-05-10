package com.itedya.skymaster.daos;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.exceptions.ServerError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class IslandDao {
    private static IslandDao instance;

    public static IslandDao getInstance() {
        if (instance == null) instance = new IslandDao();
        return instance;
    }

    private IslandDao() {
    }

    public List<IslandDto> getByOwnerUuid(String ownerUuid) throws ServerError {
        return getByOwnerUuid(ownerUuid, false);
    }

    public List<IslandDto> getByOwnerUuid(String ownerUuid, Boolean withDeleted) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "SELECT * FROM `skymaster_islands` WHERE `ownerUuid` = ?";
        if (!withDeleted) query += " AND `deletedAt` = null";

        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, ownerUuid);

            ResultSet resultSet = stmt.executeQuery();

            List<IslandDto> result = new ArrayList<>();

            while (resultSet.next()) {
                IslandDto dto = new IslandDto(resultSet);
                result.add(dto);
            }

            connection.close();

            return result;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public int getCount() throws ServerError {
        return getCount(false);
    }

    public int getCount(Boolean withDeleted) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "SELECT COUNT(*) as `size` FROM skymaster_islands";

        if (!withDeleted) query += " WHERE deletedAt != null";

        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();

            int result;

            if (rs.next()) {
                result = rs.getInt("size");
            } else {
                throw new SQLException("No rows, something went wrong. IslandDao:77");
            }

            connection.close();

            return result;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public void create(IslandDto islandDto) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "INSERT INTO `skymaster_islands` SET ownerUuid = ?, schematicId = ?";

        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setString(1, islandDto.getOwnerUuid());
            stmt.setInt(2, islandDto.getSchematicId());

            stmt.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public IslandDto getById(int id) throws ServerError {
        return getById(id, false);
    }

    public IslandDto getById(int id, Boolean withDeleted) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "SELECT * FROM `skymaster_islands` WHERE `id` = ?";
        if (! withDeleted) query += " AND deletedAt = null";
        query += " LIMIT 1";

        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            connection.close();

            return new IslandDto(rs);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public void update(IslandDto islandDto) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "UPDATE `skymaster_islands` SET ownerUuid = ?, schematicId = ? WHERE id = ?";

        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setString(1, islandDto.getOwnerUuid());
            stmt.setInt(2, islandDto.getSchematicId());
            stmt.setInt(3, islandDto.getId());

            ResultSet rs = stmt.executeQuery();

            connection.close();

            new IslandDto(rs);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }
}
