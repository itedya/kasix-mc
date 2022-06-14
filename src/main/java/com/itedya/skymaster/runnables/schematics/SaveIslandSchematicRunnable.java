package com.itedya.skymaster.runnables.schematics;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.dtos.IslandSchematicDto;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;

public class SaveIslandSchematicRunnable extends BukkitRunnable {
    private Connection connection;
    private final Conversable conversable;
    private final IslandSchematicDto dto;

    public SaveIslandSchematicRunnable(Conversable conversable, IslandSchematicDto dto) {
        this.conversable = conversable;
        this.dto = dto;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            conversable.sendRawMessage(ChatColor.RED + "Wystąpił błąd serwera!");
            return;
        }

        try {
            IslandSchematicDao islandSchematicDao = new IslandSchematicDao(connection);
            islandSchematicDao.create(dto);

            connection.commit();
            connection.close();

            conversable.sendRawMessage(ChatColor.GREEN + "Pomyślnie zapisano schemat.");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            conversable.sendRawMessage(ChatColor.RED + "Wystąpił błąd serwera.");
        }
    }
}
