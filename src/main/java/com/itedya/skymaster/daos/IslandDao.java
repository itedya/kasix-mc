package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.dtos.IslandSchematicDto;
import org.bukkit.Material;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IslandDao {
    private final Connection connection;

    public IslandDao(Connection connection) {
        this.connection = connection;
    }

    public int getSumByOwnerUuid(String ownerUuid) throws SQLException {
        return getSumByOwnerUuid(ownerUuid, false);
    }

    public int getSumByOwnerUuid(String ownerUuid, Boolean withDeleted) throws SQLException {
        String query = "SELECT count(*) FROM `skymaster_islands` WHERE `ownerUuid` = ?";
        if (!withDeleted) query += " AND `deletedAt` = null";

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setString(1, ownerUuid);

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

    public List<IslandDto> getByOwnerUuid(String ownerUuid) throws SQLException {
        return getByOwnerUuid(ownerUuid, false);
    }

    public List<IslandDto> getByOwnerUuid(String ownerUuid, Boolean withDeleted) throws SQLException {
        String query = "SELECT * FROM `skymaster_islands` WHERE `ownerUuid` = ?";
        if (!withDeleted) query += " AND `deletedAt` IS NULL";

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
    }

    public int getCount() throws SQLException {
        return getCount(false);
    }

    public int getCount(Boolean withDeleted) throws SQLException {
        String query = "SELECT COUNT(*) as `size` FROM skymaster_islands";

        if (!withDeleted) query += " WHERE deletedAt IS NULL";

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
    }

    public IslandDto create(IslandDto islandDto) throws SQLException {
        String query = "INSERT INTO `skymaster_islands` SET ownerUuid = ?, schematicId = ?, name = ?, radius = ?";

        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, islandDto.getOwnerUuid());
        stmt.setInt(2, islandDto.getSchematicId());
        stmt.setString(3, islandDto.getName());
        stmt.setInt(4, islandDto.getRadius());

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) throw new SQLException("No rows affected!");

        ResultSet rs = stmt.getGeneratedKeys();

        if (rs.next()) islandDto.setId(rs.getInt(1));
        else throw new SQLException("No id generated for added island!");

        rs.close();
        stmt.close();

        return islandDto;
    }

    public IslandDto getById(int id) throws SQLException {
        return getById(id, false);
    }

    public IslandDto getById(int id, Boolean withDeleted) throws SQLException {
        String query = "SELECT * FROM `skymaster_islands` WHERE `id` = ?";
        if (!withDeleted) query += " AND deletedAt IS NULL";
        query += " LIMIT 1";

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        IslandDto result = (rs.next()) ? new IslandDto(rs) : null;

        rs.close();
        stmt.close();

        return result;
    }

    public void update(IslandDto islandDto) throws SQLException {
        String query = "UPDATE `skymaster_islands` SET ownerUuid = ?, schematicId = ?, name = ?, radius = ? WHERE id = ?";

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setString(1, islandDto.getOwnerUuid());
        stmt.setInt(2, islandDto.getSchematicId());
        stmt.setString(3, islandDto.getName());
        stmt.setInt(4, islandDto.getRadius());
        stmt.setInt(5, islandDto.getId());

        stmt.executeUpdate();
        stmt.close();
    }

    public void removeById(Integer islandId) throws SQLException {
        String query = "UPDATE `skymaster_islands` SET deletedAt = CURRENT_TIMESTAMP WHERE id = ?";

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, islandId);

        stmt.executeUpdate();
        stmt.close();
    }

    public List<IslandDto> getByOwnerUuidWithAllRelations(String ownerUuid) throws SQLException {
        return getByOwnerUuidWithAllRelations(ownerUuid, false);
    }

    public List<IslandDto> getByOwnerUuidWithAllRelations(String ownerUuid, Boolean withDeleted) throws SQLException {
        String query = "SELECT skymaster_islands.*, skymaster_homes.*, skymaster_schematics.* " +
                "FROM `skymaster_islands` " +
                "         JOIN skymaster_island_has_homes ON skymaster_islands.id = skymaster_island_has_homes.islandId " +
                "         JOIN skymaster_homes ON skymaster_homes.id = skymaster_island_has_homes.homeId " +
                "         JOIN skymaster_schematics ON skymaster_schematics.id = skymaster_islands.schematicId " +
                "WHERE skymaster_islands.ownerUuid = ?";

        if (!withDeleted) query += " AND skymaster_islands.deletedAt IS NULL";

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, ownerUuid);
        ResultSet rs = stmt.executeQuery();

        List<IslandDto> result = new ArrayList<>();

        while (rs.next()) {
            IslandDto islandDto = new IslandDto();

            islandDto.setId(rs.getInt("skymaster_islands.id"));
            islandDto.setName(rs.getString("skymaster_islands.name"));
            islandDto.setOwnerUuid(rs.getString("ownerUuid"));
            islandDto.setRadius(rs.getInt("radius"));
            islandDto.setSchematicId(rs.getInt("schematicId"));
            islandDto.setUpdatedAt(rs.getDate("skymaster_islands.updatedAt"));
            islandDto.setCreatedAt(rs.getDate("skymaster_islands.createdAt"));
            islandDto.setDeletedAt(rs.getDate("skymaster_islands.deletedAt"));

            IslandHomeDto islandHomeDto = new IslandHomeDto();
            islandHomeDto.setId(rs.getInt("skymaster_homes.id"));
            islandHomeDto.setX(rs.getInt("x"));
            islandHomeDto.setY(rs.getInt("y"));
            islandHomeDto.setZ(rs.getInt("z"));
            islandHomeDto.setWorldUuid(rs.getString("worldUuid"));
            islandHomeDto.setCreatedAt(rs.getDate("skymaster_homes.createdAt"));
            islandHomeDto.setUpdatedAt(rs.getDate("skymaster_homes.updatedAt"));
            islandHomeDto.setDeletedAt(rs.getDate("skymaster_homes.deletedAt"));
            islandDto.setHome(islandHomeDto);

            IslandSchematicDto schematicDto = new IslandSchematicDto();
            schematicDto.setId(rs.getInt("skymaster_schematics.id"));
            schematicDto.setName(rs.getString("skymaster_schematics.name"));
            schematicDto.setDescription(rs.getString("description"));
            schematicDto.setFilePath(rs.getString("filePath"));

            Material material = Material.valueOf(rs.getString("material"));
            schematicDto.setMaterial(material);
            schematicDto.setCreatedAt(rs.getDate("skymaster_schematics.createdAt"));
            schematicDto.setUpdatedAt(rs.getDate("skymaster_schematics.updatedAt"));
            schematicDto.setDeletedAt(rs.getDate("skymaster_schematics.deletedAt"));
            islandDto.setSchematic(schematicDto);

            result.add(islandDto);
        }

        return result;
    }

    public List<IslandDto> getByMemberUuidWithAllRelations(String memberUuid) throws SQLException {
        return getByMemberUuidWithAllRelations(memberUuid, false);
    }

    public List<IslandDto> getByMemberUuidWithAllRelations(String memberUuid, Boolean withDeleted) throws SQLException {
        String query = "SELECT skymaster_islands.*, skymaster_homes.*, skymaster_schematics.* " +
                "FROM `skymaster_island_has_members` " +
                "         JOIN skymaster_islands ON skymaster_island_has_members.islandId = skymaster_islands.id " +
                "         JOIN skymaster_island_has_homes ON skymaster_islands.id = skymaster_island_has_homes.islandId " +
                "         JOIN skymaster_homes ON skymaster_homes.id = skymaster_island_has_homes.homeId " +
                "         JOIN skymaster_schematics ON skymaster_schematics.id = skymaster_islands.schematicId " +
                "WHERE skymaster_island_has_members.playerUuid = ?";

        if (!withDeleted) query += " AND skymaster_islands.deletedAt IS NULL";

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, memberUuid);
        ResultSet rs = stmt.executeQuery();

        List<IslandDto> result = new ArrayList<>();

        while (rs.next()) {
            IslandDto islandDto = new IslandDto();

            islandDto.setId(rs.getInt("skymaster_islands.id"));
            islandDto.setName(rs.getString("skymaster_islands.name"));
            islandDto.setOwnerUuid(rs.getString("ownerUuid"));
            islandDto.setRadius(rs.getInt("radius"));
            islandDto.setSchematicId(rs.getInt("schematicId"));
            islandDto.setUpdatedAt(rs.getDate("skymaster_islands.updatedAt"));
            islandDto.setCreatedAt(rs.getDate("skymaster_islands.createdAt"));
            islandDto.setDeletedAt(rs.getDate("skymaster_islands.deletedAt"));

            IslandHomeDto islandHomeDto = new IslandHomeDto();
            islandHomeDto.setId(rs.getInt("skymaster_homes.id"));
            islandHomeDto.setX(rs.getInt("x"));
            islandHomeDto.setY(rs.getInt("y"));
            islandHomeDto.setZ(rs.getInt("z"));
            islandHomeDto.setWorldUuid(rs.getString("worldUuid"));
            islandHomeDto.setCreatedAt(rs.getDate("skymaster_homes.createdAt"));
            islandHomeDto.setUpdatedAt(rs.getDate("skymaster_homes.updatedAt"));
            islandHomeDto.setDeletedAt(rs.getDate("skymaster_homes.deletedAt"));
            islandDto.setHome(islandHomeDto);

            IslandSchematicDto schematicDto = new IslandSchematicDto();
            schematicDto.setId(rs.getInt("skymaster_schematics.id"));
            schematicDto.setName(rs.getString("skymaster_schematics.name"));
            schematicDto.setDescription(rs.getString("description"));
            schematicDto.setFilePath(rs.getString("filePath"));

            Material material = Material.valueOf(rs.getString("material"));
            schematicDto.setMaterial(material);
            schematicDto.setCreatedAt(rs.getDate("skymaster_schematics.createdAt"));
            schematicDto.setUpdatedAt(rs.getDate("skymaster_schematics.updatedAt"));
            schematicDto.setDeletedAt(rs.getDate("skymaster_schematics.deletedAt"));
            islandDto.setSchematic(schematicDto);

            result.add(islandDto);
        }

        return result;
    }
}
