package com.itedya.skymaster.runnables.home;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandHomeDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;

public class SetIslandHomeRunnable extends BukkitRunnable {
    private final BlockVector3 wgPlayerLocation;
    private final Player player;
    private Connection connection;
    private IslandDto islandDto;
    private final Location playerLocation;

    public SetIslandHomeRunnable(Player player, Location playerLocation) {
        this.player = player;
        this.playerLocation = playerLocation;

        this.wgPlayerLocation = BlockVector3.at(
                playerLocation.getBlockX(),
                playerLocation.getBlockY(),
                playerLocation.getBlockZ()
        );
    }

    @Override
    public void run() {
        try {
            // get connection
            this.connection = Database.getInstance().getConnection();

            // get region manager
            RegionManager regionManager = WorldGuardUtil.getRegionManager();

            // get player region
            ProtectedRegion islandRegion = regionManager.getApplicableRegions(this.wgPlayerLocation)
                    .getRegions()
                    .stream()
                    .findFirst()
                    .orElse(null);

            // if region does not exist or region is not an island, exit with warning
            if (islandRegion == null || !islandRegion.getId().startsWith("island_")) {
                player.sendMessage(ChatColor.YELLOW + "Nie znajdujesz się na żadnej wyspie!");
                this.shutdown();
                return;
            }

            // get island id
            int islandId = Integer.parseInt(islandRegion.getId().replace("island_", ""));

            // get island dto by id
            IslandDao islandDao = new IslandDao(this.connection);
            this.islandDto = islandDao.getById(islandId);

            ThreadUtil.sync(this::checkPermissions);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }
    }

    public void checkPermissions() {
        String playerUuid = player.getUniqueId().toString();

        if ((playerUuid.equals(islandDto.getOwnerUuid()) && !player.hasPermission("skymaster.islands.set-home")) &&
                (!playerUuid.equals(islandDto.getOwnerUuid()) && !player.hasPermission("skymaster.islands.set-home-someone"))) {
            player.sendMessage(ChatColor.RED + "Brak permisji!");
            return;
        }

        ThreadUtil.async(this::saveData);
    }

    public void saveData() {
        try {
            Location homeLocationNormalized = new Location(
                    playerLocation.getWorld(),
                    playerLocation.getBlockX(),
                    playerLocation.getBlockY(),
                    playerLocation.getBlockZ()
            );

            IslandHomeDao islandHomeDao = new IslandHomeDao(connection);

            IslandHomeDto islandHomeDto = new IslandHomeDto();
            islandHomeDto.setWorldUuid(playerLocation.getWorld().getUID().toString());
            islandHomeDto.setX(playerLocation.getBlockX());
            islandHomeDto.setY(playerLocation.getBlockY());
            islandHomeDto.setZ(playerLocation.getBlockZ());
            islandHomeDao.updateByIslandId(islandHomeDto.getId(), islandHomeDto);


            player.sendMessage(ChatColor.GREEN + "Zaktualizowano dom wyspy na lokalizacje " +
                    "X:" + homeLocationNormalized.getBlockX() + " " +
                    "Y:" + homeLocationNormalized.getBlockY() + " " +
                    "Z:" + homeLocationNormalized.getBlockZ()
            );
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera!");
        }
    }

    public void shutdown() {
        if (connection != null) {
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
