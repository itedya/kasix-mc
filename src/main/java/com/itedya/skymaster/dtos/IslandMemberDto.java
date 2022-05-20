package com.itedya.skymaster.dtos;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IslandMemberDto {
    public IslandMemberDto() {
    }

    public IslandMemberDto(ResultSet rs) throws SQLException {
        this.setPlayerUuid(rs.getString("playerUuid"));
        this.setIslandId(rs.getInt("islandId"));
        this.setCreatedAt(rs.getDate("createdAt"));
        this.setUpdatedAt(rs.getDate("updatedAt"));
        this.setDeletedAt(rs.getDate("deletedAt"));
    }

    private String playerUuid;
    private Integer islandId;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    public String getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(String playerUuid) {
        this.playerUuid = playerUuid;
    }

    public Integer getIslandId() {
        return islandId;
    }

    public void setIslandId(Integer islandId) {
        this.islandId = islandId;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
