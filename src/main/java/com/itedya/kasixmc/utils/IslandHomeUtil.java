package com.itedya.kasixmc.utils;

import com.itedya.kasixmc.dtos.IslandDto;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class IslandHomeUtil {
    private static Map<String, Location> lastPlayerLocationMap = new HashMap<>();
    private static Map<String, Integer> playerTicks = new HashMap<>();

    public static void addPlayerToQueue(Player player, IslandDto islandDto) {
        if (player.hasPermission("kasix-mc.islands.teleport-instantly")) {
            player.teleport(islandDto.getHome());
            player.sendMessage(ChatColor.GREEN + "Teleportacja do domu wyspy!");
            return;
        }

        player.sendMessage(ChatColor.YELLOW + "Za 5 sekund nastąpi teleportacja, " + ChatColor.GOLD + "nie ruszaj się!");

        String uuid = player.getUniqueId().toString();

        lastPlayerLocationMap.put(uuid, player.getLocation());
        playerTicks.put(uuid, 0);

        scheduleNextSync(player, islandDto);
    }

    private static void scheduleNextSync(Player player, IslandDto islandDto) {
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
                player.teleport(islandDto.getHome());
                player.sendMessage(ChatColor.GREEN + "Teleportacja do domu wyspy!");
                lastPlayerLocationMap.remove(uuid);
                playerTicks.remove(uuid);
                return;
            }

            playerTicks.put(uuid, ticks + 10);

            scheduleNextSync(player, islandDto);
        }, 10);
    }
}
