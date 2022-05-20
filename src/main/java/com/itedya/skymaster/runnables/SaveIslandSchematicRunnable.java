package com.itedya.skymaster.runnables;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.dtos.IslandSchematicDto;
import com.itedya.skymaster.exceptions.ServerError;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

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
            conversable.sendRawMessage(ChatColor.GREEN + "Pomyślnie zapisano schemat.");
        } catch (ServerError e) {
            SkyMaster.getInstance().getLogger().log(Level.SEVERE, e.getMessage(), e);
            conversable.sendRawMessage(ChatColor.RED + "Wystąpił błąd serwera.");
        }
    }
}
