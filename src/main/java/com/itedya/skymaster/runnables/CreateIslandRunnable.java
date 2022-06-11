package com.itedya.skymaster.runnables;

import com.fastasyncworldedit.core.FaweAPI;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandHomeDao;
import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.dtos.IslandSchematicDto;
import com.itedya.skymaster.utils.PathUtil;
import com.itedya.skymaster.utils.PlayerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;

// RUNNABLE LAUNCHER - CreateIslandGUIHandler
public class CreateIslandRunnable extends BukkitRunnable {
    private final Player player;
    private final Integer schematicId;
    private Connection connection;
    private IslandDto islandDto;
    private org.bukkit.World world;
    private World adaptedWorld;
    private IslandSchematicDto schematicDto;
    private Clipboard clipboard;
    private double spawnAtX;
    private double spawnAtY;
    private double spawnAtZ;
    private double middleX;
    private double middleZ;
    private IslandHomeDto homeLocation;
    private final String islandName;

    public CreateIslandRunnable(Player player, Integer schematicId, String islandName) {
        this.player = player;
        this.schematicId = schematicId;
        this.islandName = islandName;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandSchematicDao islandSchematicDao = new IslandSchematicDao(connection);

            this.schematicDto = islandSchematicDao.getById(this.schematicId);
            if (this.schematicDto == null) return;

            player.sendMessage(ChatColor.YELLOW + "Trwa generowanie wyspy dla ciebie! Daj serwerowi chwilkę ;)");

            this.islandDto = new IslandDto();
            this.islandDto.setOwnerUuid(player.getUniqueId().toString());
            this.islandDto.setName(islandName);
            this.islandDto.setSchematicId(schematicId);

            ThreadUtil.sync(this::getWorlds);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
        }
    }

    public void getWorlds() {
        try {
            this.world = Bukkit.getWorld("world_islands");
            this.adaptedWorld = FaweAPI.getWorld("world_islands");
            ThreadUtil.async(this::loadSchematicFile);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
        }
    }

    public void loadSchematicFile() {
        try {
            // get schematic file
            File schematicFile = new File(PathUtil.getSchematicFilePath(this.schematicDto.getFilePath()));

            // load schematic into clipboard
            this.clipboard = FaweAPI.load(schematicFile);

            ThreadUtil.async(this::measureIslandPosition);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
        }
    }

    public void measureIslandPosition() {
        try {
            IslandDao islandDao = new IslandDao(connection);

            int nth = islandDao.getCount() + 1;

            this.middleX = nth * 1000 + nth * 1000 - 1000 - 500;
            this.middleZ = 500;

            this.spawnAtX = middleX + (clipboard.getWidth() / 2.0);
            this.spawnAtY = 120;
            this.spawnAtZ = middleZ + (clipboard.getLength() / 2.0);
            ThreadUtil.sync(this::pasteClipboard);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
        }
    }

    public void pasteClipboard() {
        clipboard.paste(adaptedWorld, BlockVector3.at(spawnAtX, spawnAtY, spawnAtZ), true, false, true, null);

        double middleY = world.getHighestBlockYAt((int) middleX, (int) middleZ) + 1;

        homeLocation = new IslandHomeDto();
        homeLocation.setX((int) middleX);
        homeLocation.setY((int) middleY);
        homeLocation.setZ((int) middleZ);
        homeLocation.setWorldUuid(world.getUID().toString());

        ThreadUtil.async(this::saveIntoDatabase);
    }

    public void saveIntoDatabase() {
        try {
            IslandDao islandDao = new IslandDao(connection);
            islandDao.create(islandDto);

            IslandHomeDao islandHomeDao = new IslandHomeDao(connection);
            islandHomeDao.create(islandDto.getId(), homeLocation);

            ThreadUtil.sync(this::createIsland);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }
    }

    public void createIsland() {
        try {
            int radius = PlayerUtil.getStartIslandRadius(player);

            ProtectedRegion region = WorldGuardUtil.createRegionWithoutSaving(
                    "island_" + islandDto.getId(),
                    BlockVector3.at(homeLocation.getX() - radius, -64, homeLocation.getZ() + radius),
                    BlockVector3.at(homeLocation.getX() + radius, 319, homeLocation.getZ() - radius)
            );

            WorldGuardUtil.resetRegionFlags(region);
            WorldGuardUtil.resetRegionMembers(region, islandDto, new ArrayList<>());
            WorldGuardUtil.resetPriority(region);

            if (WorldGuardUtil.doesCuboidIntersect(region)) {
                throw new Exception("Cuboid intersects with existing region " + region.getId());
            }

            RegionManager manager = WorldGuardUtil.getRegionManager();
            manager.addRegion(region);

            ThreadUtil.async(this::commit);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
        }
    }

    public void commit() {
        try {
            connection.commit();
            connection.close();

            ThreadUtil.sync(this::teleport);
        } catch (Exception e) {
            e.printStackTrace();
            this.shutdown();
        }
    }

    public void teleport() {
        player.sendMessage(ChatColor.GREEN + "Wygenerowano na koordynatach: " + spawnAtX + " " + spawnAtY + " " + spawnAtZ);
        player.teleport(new Location(world, homeLocation.getX(), homeLocation.getY(), homeLocation.getZ()));
    }

    public void shutdown() {
        if (connection != null) {
            try {
                connection.rollback();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
