package com.itedya.skymaster.runnables.island;

import com.fastasyncworldedit.core.FaweAPI;
import com.itedya.skymaster.daos.*;
import com.itedya.skymaster.dtos.IslandCreationCooldownDto;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandHomeDto;
import com.itedya.skymaster.dtos.database.IslandSchematicDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.*;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

// RUNNABLE LAUNCHER - CreateIslandGUIHandler
public class CreateIslandRunnable extends SkymasterRunnable {
    private final Player player;
    private final int schematicId;
    private final String islandName;

    public CreateIslandRunnable(Player executor, Player player, Integer schematicId, String islandName) {
        super(executor, true);

        this.player = player;
        this.schematicId = schematicId;
        this.islandName = islandName;
    }

    private IslandSchematicDto schematicDto;

    @Override
    public void run() {
        IslandCreationCooldownDao dao = IslandCreationCooldownDao.getInstance();
        IslandCreationCooldownDto dto = dao.getByPlayerUuid(player.getUniqueId().toString());

        if (dto != null) {

            player.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Kolejną wyspę będziesz mógł stworzyć za: ").color(ChatColor.RED)
                    .append(dto.getStringHours()).bold(true)
                    .append(":").bold(false)
                    .append(dto.getStringMinutes()).bold(true)
                    .append(":").bold(false)
                    .append(dto.getStringSeconds()).bold(true)
                    .append(". ").bold(false)
                    .append("Jeżeli jesteś w wyjątkowej sytuacji i potrzebujesz jej szybciej to napisz na /helpop, pomożemy :)").color(ChatColor.YELLOW)
                    .create());
            return;
        }

        ThreadUtil.async(this::getSchematic);
    }

    public void getSchematic() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandSchematicDao islandSchematicDao = new IslandSchematicDao(connection);
            schematicDto = islandSchematicDao.getById(schematicId);
            if (schematicDto == null) {
                throw new Exception("Schematic with id " + schematicId + " does not exist!");
            }

            player.sendMessage(ChatColor.YELLOW + "Trwa generowanie wyspy dla ciebie! Daj serwerowi chwilkę ;)");

            ThreadUtil.sync(this::getSynchronousData);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    private org.bukkit.World world;
    private World adaptedWorld;
    private int radius;

    public void getSynchronousData() {
        try {
            world = Bukkit.getWorld("world_islands");
            adaptedWorld = FaweAPI.getWorld("world_islands");

            radius = PlayerUtil.getStartIslandRadius(player);

            ThreadUtil.async(this::createIslandInDatabase);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    private IslandDto islandDto;

    private void createIslandInDatabase() {
        try {
            islandDto = new IslandDto();
            islandDto.ownerUuid = player.getUniqueId().toString();
            islandDto.name = islandName;
            islandDto.schematicId = schematicId;
            islandDto.radius = radius;

            IslandDao islandDao = new IslandDao(connection);
            islandDto = islandDao.create(islandDto);

            ThreadUtil.async(this::loadSchematicFile);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    private Clipboard clipboard;

    public void loadSchematicFile() {
        try {
            // get schematic file
            File schematicFile = new File(PathUtil.getSchematicFilePath(schematicDto.filePath));

            // load schematic into clipboard
            clipboard = FaweAPI.load(schematicFile);

            ThreadUtil.async(this::pasteClipboard);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    private BlockVector3 spawnVector;

    public void pasteClipboard() {
        try {
            spawnVector = WorldGuardUtil.calculateIslandPosition(islandDto.id);

            var centeredSpawnVector = spawnVector.add(
                    -schematicDto.spawnOffsetX,
                    -schematicDto.spawnOffsetY,
                    -schematicDto.spawnOffsetZ
            );

            clipboard.setOrigin(clipboard.getRegion().getMinimumPoint());
            clipboard.paste(adaptedWorld, centeredSpawnVector, true, true, true, null);

            ThreadUtil.async(this::createHomeInDatabase);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    private IslandHomeDto homeDto;

    public void createHomeInDatabase() {
        try {
            homeDto = new IslandHomeDto();
            homeDto.x = spawnVector.getX();
            homeDto.y = spawnVector.getY();
            homeDto.z = spawnVector.getZ();
            homeDto.worldUuid = world.getUID().toString();

            var dao = new IslandHomeDao(connection);
            dao.create(islandDto.id, homeDto);

            ThreadUtil.sync(this::createRegion);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void createRegion() {
        try {
            ProtectedRegion region = WorldGuardUtil.createRegionWithoutSaving(
                    "island_" + islandDto.id,
                    BlockVector3.at(homeDto.x - radius, -64, homeDto.z + radius),
                    BlockVector3.at(homeDto.x + radius, 319, homeDto.z - radius)
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
            super.errorHandling(e);
        }
    }

    public void commit() {
        try {
            connection.commit();
            connection.close();

            ThreadUtil.sync(this::teleport);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void teleport() {
        try {
            player.sendMessage("%sWygenerowano na koordynatach: X:%s Y:%s Z:%s".formatted(ChatColor.GREEN, homeDto.x, homeDto.y, homeDto.z));
            player.teleport(new Location(world, homeDto.x, homeDto.y, homeDto.z));

            this.addCooldown();
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void addCooldown() {
        if (player.hasPermission("skymaster.islands.omit-creation-cooldown")) {
            return;
        }
        IslandCreationCooldownDao dao = IslandCreationCooldownDao.getInstance();

        IslandCreationCooldownDto dto = new IslandCreationCooldownDto();
        dto.playerUuid = player.getUniqueId().toString();
        dto.expiresIn = 60 * 60 * 24;
        dao.add(dto);
    }
}
