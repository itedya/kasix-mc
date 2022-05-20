CREATE TABLE IF NOT EXISTS skymaster_island_has_members
(
    playerUuid VARCHAR(255)                                                   NOT NULL,
    islandId   INT                                                            NOT NULL,
    updatedAt  DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    createdAt  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    deletedAt  DATETIME,
    FOREIGN KEY (islandId) REFERENCES skymaster_islands (id)
);

