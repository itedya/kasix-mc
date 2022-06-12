CREATE TABLE IF NOT EXISTS skymaster_homes
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    x         INT                                                            NOT NULL,
    y         INT                                                            NOT NULL,
    z         INT                                                            NOT NULL,
    worldUuid VARCHAR(255)                                                   NOT NULL,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    deletedAt DATETIME
);