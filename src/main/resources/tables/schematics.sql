CREATE TABLE IF NOT EXISTS skymaster_schematics
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(255)                                                   NOT NULL,
    description  TEXT(1000)                                                     NOT NULL,
    filePath     TEXT(1000)                                                     NOT NULL,
    spawnOffsetX INT                                                            NOT NULL,
    spawnOffsetY INT                                                            NOT NULL,
    spawnOffsetZ INT                                                            NOT NULL,
    material     VARCHAR(255)                                                   NOT NULL,
    updatedAt    DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    createdAt    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    deletedAt    DATETIME
);