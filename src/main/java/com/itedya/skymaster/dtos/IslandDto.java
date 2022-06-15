package com.itedya.skymaster.dtos;

import org.bukkit.Location;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class IslandDto {
    private int id;

    private String name;

    private String ownerUuid;

    private int schematicId;

    private Date updatedAt;

    private Date createdAt;

    private Date deletedAt;

    private IslandHomeDto home;
    private IslandSchematicDto schematic;
    private List<IslandMemberDto> members;

    public IslandDto() {

    }

    public IslandDto(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.name = resultSet.getString("name");
        this.ownerUuid = resultSet.getString("ownerUuid");
        this.schematicId = resultSet.getInt("schematicId");
        this.createdAt = resultSet.getDate("createdAt");
        this.updatedAt = resultSet.getDate("updatedAt");
        this.deletedAt = resultSet.getDate("deletedAt");
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public IslandHomeDto getHome() {
        return home;
    }

    public void setHome(IslandHomeDto home) {
        this.home = home;
    }

    public IslandSchematicDto getSchematic() {
        return schematic;
    }

    public void setSchematic(IslandSchematicDto schematic) {
        this.schematic = schematic;
    }

    public void setMembers(List<IslandMemberDto> members) {
        this.members = members;
    }

    public List<IslandMemberDto> getMembers() {
        return members;
    }
}