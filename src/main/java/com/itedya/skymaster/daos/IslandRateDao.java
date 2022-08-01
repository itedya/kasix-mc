package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.database.IslandHomeDto;
import com.itedya.skymaster.dtos.database.IslandMemberDto;
import com.itedya.skymaster.dtos.database.IslandRateDto;
import com.itedya.skymaster.enums.IslandRate;
import com.itedya.skymaster.utils.sql.IslandHomeDaoSqlUtil;
import com.itedya.skymaster.utils.sql.IslandMemberDaoSqlUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IslandRateDao {
    private final Connection connection;

    public IslandRateDao(Connection connection) {
        this.connection = connection;
    }

    // Object as IslandRateDto
    public List<IslandRateDto> getByIslandId(Integer islandId) throws SQLException {
        return getByIslandId(islandId, false);
    }

    public List<IslandRateDto> getByIslandId(Integer islandId, boolean withDeleted) throws SQLException {
        String query;
        if (withDeleted) {
            query = IslandMemberDaoSqlUtil.GET_BY_ISLAND_ID_WITH_DELETED;
        } else {
            query = IslandMemberDaoSqlUtil.GET_BY_ISLAND_ID;
        }
        PreparedStatement statement = this.connection.prepareStatement(query);
        statement.setInt(1,islandId);
        ResultSet rs = statement.executeQuery();

        List<IslandRateDto> result = new ArrayList<>();

        while (rs.next()) {
            result.add(IslandRateDto.fromResultSet(rs));
        }
        rs.close();
        statement.close();

        return result;
    }
    public IslandRateDto getByIslandId_ratingPlayerUUID(Integer islandId, String ratingPlayerUUID, boolean withDeleted) throws SQLException {
        String query;
        if (withDeleted) {
            query = IslandMemberDaoSqlUtil.GET_BY_ISLAND_ID_WITH_DELETED;
        } else {
            query = IslandMemberDaoSqlUtil.GET_BY_ISLAND_ID;
        }
        PreparedStatement statement = this.connection.prepareStatement(query);
        statement.setInt(1,islandId);
        statement.setString(2,ratingPlayerUUID);

        ResultSet rs = statement.executeQuery();

        IslandRateDto result = IslandRateDto.fromResultSet(rs);

        rs.close();
        statement.close();
        return result;
    }

    public IslandRateDto create(int islandId, String ratingPlayerUUID, int rateValue) throws SQLException {
        String query = IslandHomeDaoSqlUtil.CREATE;
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        stmt.setInt(1, islandId);
        stmt.setString(2, ratingPlayerUUID);
        stmt.setInt(3, rateValue);

        int affectedRows = stmt.executeUpdate();

        if (affectedRows == 0) throw new SQLException("No rows affected!");

        IslandRateDto dto = new IslandRateDto();
        dto.value = rateValue;
        dto.islandId = islandId;
        dto.ratingPlayerUUID = ratingPlayerUUID;

        stmt.close();
        return dto;
    }
}
