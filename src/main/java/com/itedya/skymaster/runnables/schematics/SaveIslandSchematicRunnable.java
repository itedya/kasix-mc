package com.itedya.skymaster.runnables.schematics;

import com.github.slugify.Slugify;
import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.dtos.database.IslandSchematicDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class SaveIslandSchematicRunnable extends SkymasterRunnable {
    private IslandSchematicDto dto;
    private Clipboard clipboard;

    public SaveIslandSchematicRunnable(Player executor, IslandSchematicDto islandSchematicDto, Clipboard clipboard) {
        super(executor, true);

        this.dto = islandSchematicDto;
        this.clipboard = clipboard;
    }

    @Override
    public void run() {
        try {
            SkyMaster plugin = SkyMaster.getInstance();

            Path schematicsPath = Paths.get(plugin.getDataFolder().toString(), "schematics");
            schematicsPath.toFile().mkdirs();

            ThreadUtil.async(this::saveSchematicFile);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void saveSchematicFile() {
        try {
            SkyMaster plugin = SkyMaster.getInstance();

            dto.filePath = generateFileName();

            Path schematicPath = Paths.get(plugin.getDataFolder().toString(), "schematics", dto.filePath);
            ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schematicPath.toFile()));

            writer.write(clipboard);
            writer.close();

            ThreadUtil.async(this::saveIntoDatabase);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void saveIntoDatabase() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandSchematicDao islandSchematicDao = new IslandSchematicDao(connection);
            islandSchematicDao.create(dto);

            connection.commit();
            connection.close();

            executor.sendRawMessage(ChatUtil.PREFIX + " " +
                    "%sZapisano schemat." .formatted(ChatColor.GREEN));
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public String generateFileName() {
        final Slugify slg = Slugify.builder().build();
        final String slug = slg.slugify(dto.name);
        final String uuid = UUID.randomUUID().toString();

        return slug + "-" + uuid + ".schem";
    }
}
