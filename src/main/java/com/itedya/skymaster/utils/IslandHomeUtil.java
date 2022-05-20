package com.itedya.skymaster.utils;

import com.itedya.skymaster.dtos.IslandHomeDto;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IslandHomeUtil {
    private static Map<String, Location> lastPlayerLocationMap = new HashMap<>();
    private static Map<String, Integer> playerTicks = new HashMap<>();

    public static void addPlayerToQueue(Player player, IslandHomeDto home) {
        World world = Bukkit.getWorld(UUID.fromString(home.getWorldUuid()));
        Location location = new Location(world, home.getX(), home.getY(), home.getZ());

        if (player.hasPermission("kasix-mc.islands.teleport-instantly")) {
            player.teleport(location);
            player.sendMessage(ChatColor.GREEN + "Teleportacja do domu wyspy!");
            return;
        }

        player.sendMessage(ChatColor.YELLOW + "Za 5 sekund nastąpi teleportacja, " + ChatColor.GOLD + "nie ruszaj się!");

        String uuid = player.getUniqueId().toString();

        lastPlayerLocationMap.put(uuid, player.getLocation());
        playerTicks.put(uuid, 0);

        scheduleNextSync(player, location);
    }

    private static void scheduleNextSync(Player player, Location location) {
        ThreadUtil.syncDelay(() -> {
            String uuid = player.getUniqueId().toString();

            Location playerLoc = player.getLocation();
            Location lastPlayerLoc = lastPlayerLocationMap.get(uuid);

            if (playerLoc.getBlockX() != lastPlayerLoc.getBlockX() ||
                    playerLoc.getBlockY() != lastPlayerLoc.getBlockY() ||
                    playerLoc.getBlockZ() != lastPlayerLoc.getBlockZ()) {
                player.sendMessage(ChatColor.YELLOW + "Ruszyłeś się! Teleportacja anulowana.");
                lastPlayerLocationMap.remove(uuid);
                playerTicks.remove(uuid);
                return;
            }

            Integer ticks = playerTicks.get(uuid);

            if (ticks == 100) {
                player.teleport(location);
                player.sendMessage(ChatColor.GREEN + "Teleportacja do domu wyspy!");
                lastPlayerLocationMap.remove(uuid);
                playerTicks.remove(uuid);
                return;
            }

            playerTicks.put(uuid, ticks + 10);

            scheduleNextSync(player, location);
        }, 10);
    }
}
