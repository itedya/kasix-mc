CREATE TABLE IF NOT EXISTS skymaster_homes
(
    `id`        INT PRIMARY KEY AUTO_INCREMENT,
    `x`         INT                                                            NOT NULL,
    `y`         INT                                                            NOT NULL,
    `z`         INT                                                            NOT NULL,
    `worldUuid` VARCHAR(255)                                                   NOT NULL,
    `updatedAt` DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    `createdAt` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    `deletedAt` DATETIME
);

CREATE TABLE IF NOT EXISTS skymaster_schematics
(
    `id`          INT PRIMARY KEY AUTO_INCREMENT,
    `name`        VARCHAR(255)                                                   NOT NULL,
    `description` TEXT(1000)                                                     NOT NULL,
    `filePath`    TEXT(1000)                                                     NOT NULL,
    `material`    VARCHAR(255)                                                   NOT NULL,
    `updatedAt`   DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    `createdAt`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    `deletedAt`   DATETIME
);

CREATE TABLE IF NOT EXISTS skymaster_islands
(
    `id`          INT PRIMARY KEY AUTO_INCREMENT,
    `ownerUuid`   VARCHAR(255)                                                   NOT NULL,
    `schematicId` INT                                                            NOT NULL,
    `updatedAt`   DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    `createdAt`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    `deletedAt`   DATETIME,
    FOREIGN KEY (`schematicId`) REFERENCES `skymaster_schematics` (`id`)
);

CREATE TABLE IF NOT EXISTS skymaster_island_has_members
(
    `playerUuid` VARCHAR(255)                                                   NOT NULL,
    `islandId`   INT                                                            NOT NULL,
    `updatedAt`  DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    `createdAt`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    `deletedAt`  DATETIME,
    FOREIGN KEY (`islandId`) REFERENCES `skymaster_islands` (`id`)
);

CREATE TABLE IF NOT EXISTS skymaster_island_has_homes
(
    `islandId`  INT,
    `homeId`    INT,
    `updatedAt` DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    `createdAt` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    `deletedAt` DATETIME,
    FOREIGN KEY (`islandId`) REFERENCES `skymaster_islands` (`id`),
    FOREIGN KEY (`homeId`) REFERENCES `skymaster_homes` (`id`)
);