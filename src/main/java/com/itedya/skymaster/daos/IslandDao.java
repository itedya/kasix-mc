package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandHomeDto;
import com.itedya.skymaster.dtos.database.IslandSchematicDto;
import com.itedya.skymaster.utils.sql.IslandDaoSqlUtil;
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
        String query = (withDeleted) ? IslandDaoSqlUtil.GET_SUM_BY_OWNER_UUID_WITH_DELETED : IslandDaoSqlUtil.GET_SUM_BY_OWNER_UUID;

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
        String query = withDeleted ? IslandDaoSqlUtil.GET_BY_OWNER_UUID_WITH_DELETED : IslandDaoSqlUtil.GET_BY_OWNER_UUID;

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, ownerUuid);

        ResultSet resultSet = stmt.executeQuery();

        List<IslandDto> result = new ArrayList<>();

        while (resultSet.next()) {
            IslandDto dto = IslandDto.fromResultSet(resultSet);
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
        String query = withDeleted ? IslandDaoSqlUtil.GET_COUNT_WITH_DELETED : IslandDaoSqlUtil.GET_COUNT;

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
        String query = IslandDaoSqlUtil.CREATE;

        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, islandDto.ownerUuid);
        stmt.setInt(2, islandDto.schematicId);
        stmt.setString(3, islandDto.name);
        stmt.setInt(4, islandDto.radius);

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) throw new SQLException("No rows affected!");

        ResultSet rs = stmt.getGeneratedKeys();

        if (rs.next()) islandDto.id = rs.getInt(1);
        else throw new SQLException("No id generated for added island!");

        rs.close();
        stmt.close();

        return islandDto;
    }

    public IslandDto getById(int id) throws SQLException {
        return getById(id, false);
    }

    public IslandDto getById(int id, Boolean withDeleted) throws SQLException {
        String query = withDeleted ? IslandDaoSqlUtil.GET_BY_ID_WITH_DELETED : IslandDaoSqlUtil.GET_BY_ID;
        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        IslandDto result = (rs.next()) ? IslandDto.fromResultSet(rs) : null;

        rs.close();
        stmt.close();

        return result;
    }

    public void update(IslandDto islandDto) throws SQLException {
        String query = IslandDaoSqlUtil.UPDATE;

        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setString(1, islandDto.ownerUuid);
        stmt.setInt(2, islandDto.schematicId);
        stmt.setString(3, islandDto.name);
        stmt.setInt(4, islandDto.radius);
        stmt.setInt(5, islandDto.id);

        stmt.executeUpdate();
        stmt.close();
    }

    public void removeById(Integer islandId) throws SQLException {
        String query = IslandDaoSqlUtil.REMOVE_BY_ID;

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, islandId);

        stmt.executeUpdate();
        stmt.close();
    }

    public List<IslandDto> getByOwnerUuidWithAllRelations(String ownerUuid) throws SQLException {
        return getByOwnerUuidWithAllRelations(ownerUuid, false);
    }

    public List<IslandDto> getByOwnerUuidWithAllRelations(String ownerUuid, Boolean withDeleted) throws SQLException {
        String query = withDeleted ? IslandDaoSqlUtil.GET_WITH_ALL_RELATIONS_BY_OWNER_UUID_WITH_DELETED : IslandDaoSqlUtil.GET_WITH_ALL_RELATIONS_BY_OWNER_UUID;

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, ownerUuid);
        ResultSet rs = stmt.executeQuery();

        List<IslandDto> result = new ArrayList<>();

        while (rs.next()) {
            IslandDto islandDto = IslandDto.fromResultSet(rs, "island_");
            IslandHomeDto islandHomeDto = IslandHomeDto.fromResultSet(rs, "home_");
            IslandSchematicDto schematicDto = IslandSchematicDto.fromResultSet(rs, "schematic_");

            islandDto.home = islandHomeDto;
            islandDto.schematic = schematicDto;

            result.add(islandDto);
        }

        return result;
    }

    public List<IslandDto> getByMemberUuidWithAllRelations(String memberUuid) throws SQLException {
        return getByMemberUuidWithAllRelations(memberUuid, false);
    }

    public List<IslandDto> getByMemberUuidWithAllRelations(String memberUuid, Boolean withDeleted) throws SQLException {
        String query;
        if (withDeleted) {
            query = IslandDaoSqlUtil.GET_WITH_ALL_RELATIONS_BY_MEMBER_UUID_WITH_DELETED;
        } else {
            query = IslandDaoSqlUtil.GET_WITH_ALL_RELATIONS_BY_MEMBER_UUID;
        }

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, memberUuid);
        ResultSet rs = stmt.executeQuery();

        List<IslandDto> result = new ArrayList<>();

        while (rs.next()) {
            IslandDto islandDto = IslandDto.fromResultSet(rs, "island_");
            IslandHomeDto islandHomeDto = IslandHomeDto.fromResultSet(rs, "home_");
            IslandSchematicDto schematicDto = IslandSchematicDto.fromResultSet(rs, "schematic_");

            islandDto.home = islandHomeDto;
            islandDto.schematic = schematicDto;

            result.add(islandDto);
        }

        return result;
    }

    public List<IslandDto> getFirstNTHByIslandSize(int nth) throws SQLException {
        return getFirstNTHByIslandSize(false, nth);
    }

    public List<IslandDto> getFirstNTHByIslandSize(boolean withDeleted, int nth) throws SQLException {
        String query;
        if (withDeleted) {
            query = IslandDaoSqlUtil.GET_FIRST_NTH_BY_ISLAND_RADIUS_WITH_DELETED;
        } else {
            query = IslandDaoSqlUtil.GET_FIRST_NTH_BY_ISLAND_RADIUS;
        }

        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet resultSet = stmt.executeQuery();

        List<IslandDto> result = new ArrayList<>();
        List<String> existingUUIDs = new ArrayList<>();

        while (resultSet.next()) {
            var dto = IslandDto.fromResultSet(resultSet);

            var found = existingUUIDs
                    .stream()
                    .filter(uuid -> uuid.equals(dto.ownerUuid))
                    .findFirst()
                    .orElse(null);

            if (found == null) {
                result.add(dto);
                existingUUIDs.add(dto.ownerUuid);
            }
        }

        resultSet.close();
        stmt.close();

        return result;
    }
}
