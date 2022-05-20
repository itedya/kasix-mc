CREATE TABLE IF NOT EXISTS skymaster_islands
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255)                                                   NOT NULL UNIQUE,
    ownerUuid   VARCHAR(255)                                                   NOT NULL,
    schematicId INT                                                            NOT NULL,
    updatedAt   DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    createdAt   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    deletedAt   DATETIME,
    FOREIGN KEY (schematicId) REFERENCES skymaster_schematics (id)
);