CREATE TABLE IF NOT EXISTS skymaster_island_has_homes
(
    islandId  INT,
    homeId    INT,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    deletedAt DATETIME,
    FOREIGN KEY (islandId) REFERENCES skymaster_islands (id),
    FOREIGN KEY (homeId) REFERENCES skymaster_homes (id)
);