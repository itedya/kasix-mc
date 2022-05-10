package com.itedya.skymaster.dtos;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IslandHomeDto {
    public IslandHomeDto(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.x = rs.getInt("x");
        this.y = rs.getInt("y");
        this.z = rs.getInt("z");
        this.worldUuid = rs.getString("worldUuid");
        this.createdAt = rs.getDate("createdAt");
        this.updatedAt = rs.getDate("updatedAt");
        this.deletedAt = rs.getDate("deletedAt");
    }

    private int id;
    private int x;
    private int y;
    private int z;
    private String worldUuid;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorldUuid() {
        return worldUuid;
    }

    public void setWorldUuid(String worldUuid) {
        this.worldUuid = worldUuid;
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
