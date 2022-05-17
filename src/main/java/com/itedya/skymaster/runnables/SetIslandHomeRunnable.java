package com.itedya.skymaster.runnables;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandHomeDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.exceptions.ServerError;
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
    private BlockVector3 wgPlayerLocation;
    private Player player;
    private Connection connection;
    private RegionManager regionManager;
    private ProtectedRegion islandRegion;
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
            this.regionManager = WorldGuardUtil.getRegionManager();

            // get player region
            this.islandRegion = regionManager.getApplicableRegions(this.wgPlayerLocation)
                    .getRegions()
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (this.islandRegion == null || !this.islandRegion.getId().startsWith("island_")) {
                player.sendMessage(ChatColor.YELLOW + "Nie znajdujesz się na żadnej wyspie!");
                this.shutdown();
                return;
            }



            String playerUuid = player.getUniqueId().toString();

            if ((playerUuid.equals(islandDto.getOwnerUuid()) && !player.hasPermission("kasix-mc.islands.set-home")) &&
                    (!playerUuid.equals(islandDto.getOwnerUuid()) && !player.hasPermission("kasix-mc.islands.set-home-someone"))) {
                player.sendMessage(ChatColor.RED + "Brak permisji!");
                return;
            }

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
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }
    }

    public IslandDto getIsland(ProtectedRegion region) throws ServerError {
        int islandId = Integer.parseInt(region.getId().replace("island_", ""));

        IslandDao islandDao = new IslandDao(this.connection);
        this.islandDto = islandDao.getById(islandId);
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
