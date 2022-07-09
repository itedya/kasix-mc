package com.itedya.skymaster.runnables;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class SkymasterRunnable extends BukkitRunnable {
    private final Conversable executor;
    protected Connection connection;
    protected final Map<String, Object> data = new HashMap<>();

    protected SkymasterRunnable(Conversable executor, Boolean withDatabase) {
        this.executor = executor;
    }

    protected void errorHandling(Exception e) {
        e.printStackTrace();
        executor.sendRawMessage("%sWystąpił błąd serwera!"
                .formatted(ChatColor.RED));

        ThreadUtil.async(this::closeDatabase);
    }

    protected void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
