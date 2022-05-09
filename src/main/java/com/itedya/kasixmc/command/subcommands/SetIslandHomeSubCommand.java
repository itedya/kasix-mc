package com.itedya.kasixmc.command.subcommands;

import com.itedya.kasixmc.daos.IslandDao;
import com.itedya.kasixmc.dtos.IslandDto;
import com.itedya.kasixmc.utils.WorldGuardUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetIslandHomeSubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Musisz być w grze, aby wykonać tą komendę!");
                return true;
            }

            if (!player.getWorld().getName().equals("world_islands")) {
                player.sendMessage(ChatColor.YELLOW + "Jesteś w złym świecie! Przejdź na wyspy.");
                return true;
            }

            Location playerLocation = player.getLocation();
            BlockVector3 worldGuardPlayerLocation = BlockVector3.at(
                    playerLocation.getBlockX(),
                    playerLocation.getBlockY(),
                    playerLocation.getBlockZ()
            );

            RegionManager regionManager = WorldGuardUtil.getRegionManager();
            ProtectedRegion region = regionManager.getApplicableRegions(worldGuardPlayerLocation)
                    .getRegions()
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (region == null || !region.getId().startsWith("island_")) {
                player.sendMessage(ChatColor.YELLOW + "Nie znajdujesz się na żadnej wyspie!");
                return true;
            }

            String islandUuid = region.getId().replace("island_", "");

            IslandDao islandDao = IslandDao.getInstance();
            IslandDto islandDto = islandDao.getByUuid(islandUuid);
            if (islandDto == null) {
                player.sendMessage(ChatColor.RED + "Wyspa nie istnieje lub jest usunięta!");
            }

            String playerUuid = player.getUniqueId().toString();

            if ((playerUuid.equals(islandDto.getOwnerUUID()) && !player.hasPermission("kasix-mc.islands.set-home")) &&
                    (!playerUuid.equals(islandDto.getOwnerUUID()) && !player.hasPermission("kasix-mc.islands.set-home-someone"))) {
                sender.sendMessage("Brak permisji!");
                return true;
            }

            Location homeLocationNormalized = new Location(
                    playerLocation.getWorld(),
                    playerLocation.getBlockX(),
                    playerLocation.getBlockY(),
                    playerLocation.getBlockZ()
            );

            islandDto.setHome(homeLocationNormalized);
            player.sendMessage(ChatColor.GREEN + "Zaktualizowano dom wyspy na lokalizacje " +
                    "X:" + homeLocationNormalized.getBlockX() + " " +
                    "Y:" + homeLocationNormalized.getBlockY() + " " +
                    "Z:" + homeLocationNormalized.getBlockZ()
            );

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Server error.");
            return true;
        }
    }
}
