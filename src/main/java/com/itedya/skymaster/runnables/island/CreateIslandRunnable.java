package com.itedya.skymaster.runnables.island;

import com.fastasyncworldedit.core.FaweAPI;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandHomeDao;
import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandHomeDto;
import com.itedya.skymaster.dtos.database.IslandSchematicDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// RUNNABLE LAUNCHER - CreateIslandGUIHandler
public class CreateIslandRunnable extends SkymasterRunnable {

    private final Map<String, Object> data = new HashMap<>();

    public CreateIslandRunnable(Player executor, Player player, Integer schematicId, String islandName) {
        super(executor, true);

        data.put("player", player);
        data.put("schematicId", schematicId);
        data.put("islandName", islandName);
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            var schematicId = (int) data.get("schematicId");

            IslandSchematicDao islandSchematicDao = new IslandSchematicDao(connection);
            var schematicDto = islandSchematicDao.getById(schematicId);
            if (schematicDto == null) {
                throw new Exception("Schematic with id " + schematicId + " does not exist!");
            }
            data.put("schematicDto", schematicDto);

            var player = (Player) data.get("player");
            player.sendMessage(ChatColor.YELLOW + "Trwa generowanie wyspy dla ciebie! Daj serwerowi chwilkę ;)");

            ThreadUtil.sync(this::getSynchronousData);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void getSynchronousData() {
        try {
            var world = Bukkit.getWorld("world_islands");
            var adaptedWorld = FaweAPI.getWorld("world_islands");

            var player = (Player) data.get("player");
            var startRadius = PlayerUtil.getStartIslandRadius(player);

            data.put("world", world);
            data.put("adaptedWorld", adaptedWorld);
            data.put("radius", startRadius);

            ThreadUtil.async(this::createIslandInDatabase);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    private void createIslandInDatabase() {
        try {
            var player = (Player) data.get("player");
            var islandName = (String) data.get("islandName");
            var schematicId = (int) data.get("schematicId");
            var radius = (int) data.get("radius");

            var islandDto = new IslandDto();
            islandDto.ownerUuid = player.getUniqueId().toString();
            islandDto.name = islandName;
            islandDto.schematicId = schematicId;
            islandDto.radius = radius;

            IslandDao islandDao = new IslandDao(connection);
            islandDto = islandDao.create(islandDto);
            data.put("islandDto", islandDto);

            ThreadUtil.async(this::loadSchematicFile);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void loadSchematicFile() {
        try {
            var schematicDto = (IslandSchematicDto) data.get("schematicDto");

            // get schematic file
            File schematicFile = new File(PathUtil.getSchematicFilePath(schematicDto.filePath));

            // load schematic into clipboard
            var clipboard = FaweAPI.load(schematicFile);

            data.put("clipboard", clipboard);

            ThreadUtil.async(this::pasteClipboard);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void pasteClipboard() {
        try {
            var clipboard = (Clipboard) data.get("clipboard");
            var adaptedWorld = (World) data.get("adaptedWorld");
            var islandDto = (IslandDto) data.get("islandDto");
            var spawnVector = WorldGuardUtil.calculateClipboardSpawnPosition(islandDto.id, clipboard);

            clipboard.paste(adaptedWorld, spawnVector, true, false, true, null);

            var homeVector = WorldGuardUtil.calculateIslandHomePosition(islandDto.id);
            data.put("homeVector", homeVector);

            ThreadUtil.async(this::createHomeInDatabase);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void createHomeInDatabase() {
        try {
            var islandDto = (IslandDto) data.get("islandDto");
            var world = (org.bukkit.World) data.get("world");
            var homeVector = (BlockVector3) data.get("homeVector");

            var homeLocation = new IslandHomeDto();
            homeLocation.x = homeVector.getX();
            homeLocation.y = homeVector.getY();
            homeLocation.z = homeVector.getZ();
            homeLocation.worldUuid = world.getUID().toString();

            var dao = new IslandHomeDao(connection);
            dao.create(islandDto.id, homeLocation);

            data.put("homeLocation", homeLocation);

            ThreadUtil.sync(this::createRegion);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void createRegion() {
        try {
            var islandDto = (IslandDto) data.get("islandDto");
            var homeLocation = (IslandHomeDto) data.get("homeLocation");
            var radius = (int) data.get("radius");

            ProtectedRegion region = WorldGuardUtil.createRegionWithoutSaving(
                    "island_" + islandDto.id,
                    BlockVector3.at(homeLocation.x - radius, -64, homeLocation.z + radius),
                    BlockVector3.at(homeLocation.x + radius, 319, homeLocation.z - radius)
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
            var player = (Player) data.get("player");
            var world = (org.bukkit.World) data.get("world");
            var homeLocation = (IslandHomeDto) data.get("homeLocation");

            player.sendMessage("%sWygenerowano na koordynatach: X:%s Y:%s Z:%s".formatted(ChatColor.GREEN, homeLocation.x, homeLocation.y, homeLocation.z));
            player.teleport(new Location(world, homeLocation.x, homeLocation.y, homeLocation.z));
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
