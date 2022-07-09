CREATE TABLE IF NOT EXISTS skymaster_view_blocks
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    islandId          INT                                                            NOT NULL,
    blockedPlayerUuid VARCHAR(255)                                                   NOT NULL,
    updatedAt         DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    createdAt         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    deletedAt         DATETIME,
    FOREIGN KEY (islandId) REFERENCES skymaster_islands (id)
);