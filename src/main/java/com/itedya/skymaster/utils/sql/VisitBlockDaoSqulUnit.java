package com.itedya.skymaster.utils.sql;

public class VisitBlockDaoSqulUnit {
    public static final String CREATE = "INSERT INTO `skymaster_visit_blocks` SET islandOwnerUuid = ?, blockedPlayerUuid = ?";
    public static final String REMOVE = "UPDATE `skymaster_island_has_members` SET deletedAt = CURRENT_TIMESTAMP WHERE islandOwnerUuid = ? AND blockedPlayerUuid = ? AND deletedAt IS NULL";
}
