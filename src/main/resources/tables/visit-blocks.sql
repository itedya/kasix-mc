CREATE TABLE IF NOT EXISTS skymaster_visit_blocks
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    islandOwnerUuid   VARCHAR(255)                                                   NOT NULL,
    blockedPlayerUuid VARCHAR(255)                                                   NOT NULL,
    updatedAt         DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    createdAt         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    deletedAt         DATETIME
);