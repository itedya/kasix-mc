package com.itedya.kasixmc.listeners;

import com.fastasyncworldedit.core.FaweAPI;
import com.itedya.kasixmc.daos.IslandDao;
import com.itedya.kasixmc.dtos.IslandDto;
import com.itedya.kasixmc.dtos.IslandSchematicDto;
import com.itedya.kasixmc.utils.*;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;

import java.io.File;
import java.util.UUID;

public class CreateIslandGUIHandler implements Listener {
    private final String guiTitle = ConfigUtil.getColouredString("messages.islandSchematics", "&dSchematy wysp");

    @EventHandler()
    public void onInvClick(InventoryClickEvent event) {
        if (!guiTitle.equals(event.getView().getTitle())) return;

        event.setCancelled(true);

        try {
            if (!(event.getWhoClicked() instanceof Player player)) {
                return;
            }

            Material itemType = event.getCurrentItem().getType();

            IslandSchematicDto schematicDto = IslandSchematicUtil.getWithPermission((Player) event.getWhoClicked())
                    .stream()
                    .filter(ele -> ele.getMaterial().equals(itemType))
                    .findFirst()
                    .orElse(null);

            if (schematicDto == null) return;

            player.sendMessage(ChatColor.YELLOW + "Generowanie wyspy dla ciebie! Poczekaj chwilkę...");
            event.getInventory().close();

            IslandDto islandDto = new IslandDto();

            islandDto.setOwnerUUID(event.getWhoClicked().getUniqueId().toString());
            islandDto.setDeleted(false);
            islandDto.setUuid(UUID.randomUUID().toString());

            org.bukkit.World world = Bukkit.getWorld("world_islands");
            World adaptedWorld = FaweAPI.getWorld("world_islands");

            // get schematic file
            File schematicFile = new File(PathUtil.getSchematicFilePath(schematicDto.getFilePath()));

            // load schematic into clipboard
            Clipboard clipboard = FaweAPI.load(schematicFile);

            IslandDao islandDao = IslandDao.getInstance();
            int nth = islandDao.getCount() + 1;

            double middleX = nth * 1000 + nth * 1000 - 1000 - 500;
            double middleZ = 500;

            double spawnAtX = middleX + (clipboard.getWidth() / 2.0);
            double spawnAtY = 120;
            double spawnAtZ = middleZ + (clipboard.getLength() / 2.0);

            clipboard.paste(adaptedWorld, BlockVector3.at(spawnAtX, spawnAtY, spawnAtZ), true, false, true, null);

            double middleY = world.getHighestBlockYAt((int) middleX, (int) middleZ) + 1;

            Location homeLocation = new Location(world, middleX, middleY, middleZ);

            islandDto.setHome(homeLocation);

            int radius = PlayerUtil.getStartIslandRadius(player);

            ProtectedRegion region = WorldGuardUtil.createRegionWithoutSaving(
                    "island_" + islandDto.getUuid(),
                    BlockVector3.at(homeLocation.getX() - radius, -64, homeLocation.getZ() + radius),
                    BlockVector3.at(homeLocation.getX() + radius, 319, homeLocation.getZ() - radius)
            );

            WorldGuardUtil.resetRegionFlags(region);
            WorldGuardUtil.resetRegionMembers(region, islandDto);
            WorldGuardUtil.resetPriority(region);

            if (WorldGuardUtil.doesCuboidIntersect(region)) {
                throw new Exception("Cuboid intersects with existing region " + region.getId());
            }

            RegionManager manager = WorldGuardUtil.getRegionManager();
            manager.addRegion(region);

            islandDao.create(islandDto);

            player.sendMessage(ChatColor.GREEN + "Wygenerowano na koordynatach: " + spawnAtX + " " + spawnAtY + " " + spawnAtZ);
            player.teleport(homeLocation);
        } catch (Exception e) {
            e.printStackTrace();
            event.getInventory().close();
            event.getWhoClicked().sendMessage(ChatColor.RED + "Server error.");
        }
    }
}
