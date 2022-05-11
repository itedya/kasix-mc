package com.itedya.skymaster.daos;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.exceptions.ServerError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class IslandHomeDao {
    private static IslandHomeDao instance;

    public static IslandHomeDao getInstance() {
        if (instance == null) instance = new IslandHomeDao();
        return instance;
    }

    private IslandHomeDao() {
    }

    public IslandHomeDto firstByIslandId(int id) throws ServerError {
        return firstByIslandId(id, false);
    }

    public IslandHomeDto firstByIslandId(int id, Boolean withDeleted) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "SELECT * FROM `skymaster_homes` WHERE id = ?";
        if (!withDeleted) query += " AND deletedAt != null";
        query += " LIMIT 1";

        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            connection.close();

            if (rs.next()) {
                return new IslandHomeDto(rs);
            }

            return null;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }

    public void create(IslandHomeDto islandHomeDto) throws ServerError {
        SkyMaster plugin = SkyMaster.getInstance();

        String query = "INSERT INTO `skymaster_homes` SET x = ?, y = ?, z = ?, worldUuid = ?";

        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);

            stmt.setInt(1, islandHomeDto.getX());
            stmt.setInt(2, islandHomeDto.getY());
            stmt.setInt(3, islandHomeDto.getZ());
            stmt.setString(4, islandHomeDto.getWorldUuid());

            stmt.executeUpdate();

            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database error", e);
            throw new ServerError();
        }
    }
}
