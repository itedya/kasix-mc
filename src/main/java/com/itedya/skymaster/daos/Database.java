package com.itedya.skymaster.daos;

import com.itedya.skymaster.SkyMaster;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class Database {
    private static Database instance;

    public static Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }

    private final MysqlDataSource dataSource;

    private Database() {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();

        SkyMaster plugin = SkyMaster.getInstance();

        dataSource.setServerName(plugin.getConfig().getString("database.host", "localhost"));
        dataSource.setPortNumber(plugin.getConfig().getInt("database.port", 3306));
        dataSource.setDatabaseName(plugin.getConfig().getString("database.database_name", "skymaster"));
        dataSource.setUser(plugin.getConfig().getString("database.login", "root"));
        dataSource.setPassword(plugin.getConfig().getString("database.password", "password"));

        this.dataSource = dataSource;
    }

    public MysqlDataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public void migrate() {
        SkyMaster plugin = SkyMaster.getInstance();

        InputStream tablesFileStream = plugin.getResource("tables.sql");
        if (tablesFileStream == null) {
            plugin.getLogger().severe("Can't get tables.sql migration file! This is probably a bug this should not happen, please report it on github.");
        }

        try {
            String migrationQueries = tablesFileStream.readAllBytes().toString();

            Connection connection = this.getConnection();

            PreparedStatement stmt = connection.prepareStatement(migrationQueries);
            stmt.executeUpdate();
        } catch (IOException e) {
            plugin.getLogger().severe("Can't read migration queries. This is probably a bug, please report it on github.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Can't connect to database! Check database configuration in config.yml", e);
        }
    }

}
