package com.itedya.skymaster.dtos;

import org.bukkit.Material;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IslandSchematicDto {
    private int id;
    private String name;
    private String description;
    private String filePath;
    private Material material;
    private Date updatedAt;
    private Date createdAt;
    private Date deletedAt;

    public IslandSchematicDto(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name = rs.getString("name");
        this.description = rs.getString("description");
        this.filePath = rs.getString("filePath");
        this.material = Material.valueOf(rs.getString("material"));
        this.updatedAt = rs.getDate("updatedAt");
        this.createdAt = rs.getDate("createdAt");
        this.deletedAt = rs.getDate("deletedAt");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}
