package com.itedya.skymaster.daos;

import com.itedya.skymaster.SkyMaster;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
        Connection connection = getDataSource().getConnection();

        connection.setAutoCommit(false);

        return connection;
    }

    public static String convertStreamToString(InputStream is, String ecoding) throws IOException {
        StringBuilder sb = new StringBuilder(Math.max(16, is.available()));
        char[] tmp = new char[4096];

        try {
            InputStreamReader reader = new InputStreamReader(is, ecoding);
            for (int cnt; (cnt = reader.read(tmp)) > 0; )
                sb.append(tmp, 0, cnt);
        } finally {
            is.close();
        }
        return sb.toString();
    }

    public void migrate() {
        List<String> tables = List.of(
                "tables/homes.sql",
                "tables/schematics.sql",
                "tables/islands.sql",
                "tables/members.sql",
                "tables/homes_relations.sql",
                "tables/view-blocks.sql"
        );

        SkyMaster plugin = SkyMaster.getInstance();

        Connection connection = null;
        try {
            connection = this.getConnection();

            for (String table : tables) {
                InputStream tablesFileStream = plugin.getResource(table);
                if (tablesFileStream == null) {
                    plugin.getLogger().severe("Can't get " + table + " migration file! This is probably a bug, this should not happen, please report it on github.");
                    continue;
                }

                try {
                    String migrationQueries = convertStreamToString(tablesFileStream, "UTF-8");

                    PreparedStatement stmt = connection.prepareStatement(migrationQueries);
                    stmt.executeUpdate();

                    connection.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    connection.rollback();
                }
            }

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }


    }

}
