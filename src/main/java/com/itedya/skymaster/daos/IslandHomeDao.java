package com.itedya.skymaster.daos;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.exceptions.ServerError;

import java.sql.*;
import java.util.logging.Level;

public class IslandHomeDao {
    private final Connection connection;

    public IslandHomeDao(Connection connection) {
        this.connection = connection;
    }

    public IslandHomeDto firstByIslandId(int id) throws SQLException {
        return firstByIslandId(id, false);
    }

    public IslandHomeDto firstByIslandId(int id, Boolean withDeleted) throws SQLException {
        String query = "SELECT skymaster_homes.* FROM skymaster_islands " +
                "JOIN skymaster_island_has_homes ON skymaster_islands.id = skymaster_island_has_homes.islandId " +
                "JOIN skymaster_homes ON skymaster_island_has_homes.homeId = skymaster_homes.id " +
                "WHERE skymaster_islands.id = ?";
        if (!withDeleted) query += " AND skymaster_island_has_homes.deletedAt IS NULL";
        query += " LIMIT 1";

        IslandHomeDto result = null;
        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) result = new IslandHomeDto(rs);

        rs.close();
        stmt.close();

        return result;
    }

    public IslandHomeDto create(int islandId, IslandHomeDto islandHomeDto) throws SQLException {
        String query = "INSERT INTO `skymaster_homes` SET x = ?, y = ?, z = ?, worldUuid = ?";
        String relationQuery = "INSERT INTO `skymaster_island_has_homes` SET islandId = ?, homeId = ?;";

        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        stmt.setInt(1, islandHomeDto.getX());
        stmt.setInt(2, islandHomeDto.getY());
        stmt.setInt(3, islandHomeDto.getZ());
        stmt.setString(4, islandHomeDto.getWorldUuid());

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) throw new SQLException("No rows affected!");

        ResultSet generatedKeys = stmt.getGeneratedKeys();

        if (generatedKeys.next()) islandHomeDto.setId(generatedKeys.getInt(1));
        else throw new SQLException("No id generated for added home!");

        generatedKeys.close();
        stmt.close();

        stmt = connection.prepareStatement(relationQuery);

        stmt.setInt(1, islandId);
        stmt.setInt(2, islandHomeDto.getId());

        stmt.executeUpdate();

        stmt.close();

        return islandHomeDto;
    }

    public void updateByIslandId(int islandId, IslandHomeDto islandHomeDto) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "UPDATE `skymaster_homes` " +
                "JOIN `skymaster_island_has_homes` ON `skymaster_homes`.`id` = `skymaster_island_has_homes`.`homeId` " +
                "JOIN `skymaster_islands` ON `skymaster_island_has_homes`.`islandId` = `skymaster_islands`.`id` " +
                "SET x = ?, y = ?, z = ?, worldUuid = ? WHERE `skymaster_islands`.`id` = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setInt(1, islandHomeDto.getX());
            stmt.setInt(2, islandHomeDto.getY());
            stmt.setInt(3, islandHomeDto.getZ());
            stmt.setString(4, islandHomeDto.getWorldUuid());
            stmt.setInt(5, islandId);

            stmt.executeUpdate();

            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public void delete(int id) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "UPDATE `skymaster_homes` SET deletedAt = CURRENT_TIMESTAMP WHERE id = ?;";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setInt(1, id);

            stmt.executeUpdate();

            stmt.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }
}
