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
    private final Connection connection;

    public IslandDao(Connection connection) {
        this.connection = connection;
    }

    public int getSumByOwnerUuid(String ownerUuid) throws ServerError {
        return getSumByOwnerUuid(ownerUuid, false);
    }

    public int getSumByOwnerUuid(String ownerUuid, Boolean withDeleted) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "SELECT SUM(*) FROM `skymaster_islands` WHERE `ownerUuid` = ?";
        if (!withDeleted) query += " AND `deletedAt` = null";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setString(1, ownerUuid);

            ResultSet resultSet = stmt.executeQuery();

            Integer result = null;
            if (resultSet.next()) {
                result = resultSet.getInt("SUM(*)");
            } else {
                throw new ServerError("Result set is empty IslandDao:42");
            }

            resultSet.close();
            stmt.close();

            return result;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public List<IslandDto> getByOwnerUuid(String ownerUuid) throws ServerError {
        return getByOwnerUuid(ownerUuid, false);
    }

    public List<IslandDto> getByOwnerUuid(String ownerUuid, Boolean withDeleted) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "SELECT * FROM `skymaster_islands` WHERE `ownerUuid` = ?";
        if (!withDeleted) query += " AND `deletedAt` = null";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, ownerUuid);

            ResultSet resultSet = stmt.executeQuery();

            List<IslandDto> result = new ArrayList<>();

            while (resultSet.next()) {
                IslandDto dto = new IslandDto(resultSet);
                result.add(dto);
            }

            resultSet.close();
            stmt.close();

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

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();

            int result;

            if (rs.next()) {
                result = rs.getInt("size");
                rs.close();
                stmt.close();
            } else {
                throw new SQLException("No rows, something went wrong. IslandDao:77");
            }

            rs.close();
            stmt.close();

            return result;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public void create(IslandDto islandDto) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "INSERT INTO `skymaster_islands` SET ownerUuid = ?, schematicId = ?, name = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setString(1, islandDto.getOwnerUuid());
            stmt.setInt(2, islandDto.getSchematicId());
            stmt.setString(3, islandDto.getName());

            stmt.executeUpdate();

            stmt.close();
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
        if (!withDeleted) query += " AND deletedAt = null";
        query += " LIMIT 1";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            stmt.close();

            return new IslandDto(rs);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public void update(IslandDto islandDto) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "UPDATE `skymaster_islands` SET ownerUuid = ?, schematicId = ?, name = ? WHERE id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setString(1, islandDto.getOwnerUuid());
            stmt.setInt(2, islandDto.getSchematicId());
            stmt.setString(3, islandDto.getName());
            stmt.setInt(4, islandDto.getId());

            ResultSet rs = stmt.executeQuery();

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public void removeById(Integer islandId) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "UPDATE `skymaster_islands` SET deletedAt = CURRENT_TIMESTAMP WHERE id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setInt(1, islandId);

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }
}
