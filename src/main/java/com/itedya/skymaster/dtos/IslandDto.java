package com.itedya.skymaster.dtos;

import org.bukkit.Location;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class IslandDto {
    private int id;

    private String ownerUuid;

    private int schematicId;

    private Date updatedAt;

    private Date createdAt;

    private Date deletedAt;

    public IslandDto(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.ownerUuid = resultSet.getString("ownerUuid");
        this.schematicId = resultSet.getInt("schematicId");
        this.createdAt = resultSet.getDate("createdAt");
        this.updatedAt = resultSet.getDate("updatedAt");
        this.deletedAt = resultSet.getDate("deletedAt");
    }

    public int getId() {
        return id;
    }

    public String getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public int getSchematicId() {
        return schematicId;
    }

    public void setSchematicId(int schematicId) {
        this.schematicId = schematicId;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}