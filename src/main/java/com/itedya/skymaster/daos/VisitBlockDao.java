package com.itedya.skymaster.daos;

import com.itedya.skymaster.dtos.database.ViewBlockDto;
import com.itedya.skymaster.utils.sql.IslandDaoSqlUtil;
import com.itedya.skymaster.utils.sql.VisitBlockDaoSqulUnit;

import java.sql.*;

public class VisitBlockDao {
    private final Connection connection;
    public VisitBlockDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets ViewBlockDto with given islandId and blocked player uuid
     *
     * @param islandOwnerUuid          Island ID
     * @param blockedPlayerUuid Blocked Player UUID (String)
     * @return ViewBlockDto
     */
    public ViewBlockDto get(String islandOwnerUuid, String blockedPlayerUuid) throws SQLException {
        return get(islandOwnerUuid, blockedPlayerUuid, false);
    }

    /**
     * Gets ViewBlockDto with given islandId and blocked player uuid
     *
     * @param islandOwnerUuid   Blocking Player UUID (String)
     * @param blockedPlayerUuid Blocked Player UUID (String)
     * @param withDeleted       Switch if search for deleted entries
     * @return ViewBlockDto
     */
    public ViewBlockDto get(String islandOwnerUuid, String blockedPlayerUuid, Boolean withDeleted) throws SQLException {
        String query = "SELECT * FROM skymaster_visit_blocks WHERE islandOwnerUuid = ? AND blockedPlayerUuid = ?";
        if (!withDeleted) query += " AND deletedAt IS NULL";

        var stmt = connection.prepareStatement(query);
        stmt.setString(1, islandOwnerUuid);
        stmt.setString(2, blockedPlayerUuid);

        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) return null;
        var dto = ViewBlockDto.fromResultSet(rs);

        rs.close();
        stmt.close();

        return dto;
    }
    public ViewBlockDto create(ViewBlockDto blockRequest ) throws SQLException {
        String query = VisitBlockDaoSqulUnit.CREATE;

        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, blockRequest.islandOwnerUuid);
        stmt.setString(2, blockRequest.blockedPlayerUuid);

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) throw new SQLException("No rows affected!");

        ResultSet rs = stmt.getGeneratedKeys();

        if (rs.next()) blockRequest.id = rs.getInt(1);
        else throw new SQLException("No id generated for added island!");

        rs.close();
        stmt.close();

        return blockRequest;
    }
}
