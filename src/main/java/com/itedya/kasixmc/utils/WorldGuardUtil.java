package com.itedya.kasixmc.utils;

import com.fastasyncworldedit.core.FaweAPI;
import com.itedya.kasixmc.dtos.IslandDto;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldGuardUtil {
    public static ProtectedRegion createRegion(String regionName, BlockVector3 from, BlockVector3 to) {
        ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionName, from, to);
        RegionManager regionManager = getRegionManager();
        regionManager.addRegion(protectedRegion);
        return protectedRegion;
    }

    public static ProtectedRegion createRegionWithoutSaving(String regionName, BlockVector3 from, BlockVector3 to) {
        return new ProtectedCuboidRegion(regionName, from, to);
    }

    public static ProtectedRegion resetRegionFlags(ProtectedRegion protectedRegion) {
        protectedRegion.setFlags(new HashMap<>());
        protectedRegion.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
        return protectedRegion;
    }

    public static ProtectedRegion resetRegionMembers(ProtectedRegion protectedRegion, IslandDto islandDto) {
        DefaultDomain members = protectedRegion.getMembers();
        members.removeAll();
        members.addPlayer(UUID.fromString(islandDto.getOwnerUUID()));
        protectedRegion.setMembers(members);
        return protectedRegion;
    }

    public static ProtectedRegion resetPriority(ProtectedRegion protectedRegion) {
        protectedRegion.setPriority(5);
        return protectedRegion;
    }

    public static RegionManager getRegionManager() {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = FaweAPI.getWorld("world_islands");
        return container.get(world);
    }

    public static void removeRegionForDto(IslandDto islandDto) {
        RegionManager manager = getRegionManager();
        ProtectedRegion protectedRegion = getRegionForDto(islandDto);
        manager.removeRegion(protectedRegion.getId());
    }

    public static Map<String, ProtectedRegion> getAllRegions() {
        RegionManager regionManager = getRegionManager();
        return regionManager.getRegions();
    }

    public static ProtectedRegion getRegionForDto(IslandDto islandDto) {
        return getAllRegions()
                .values()
                .stream()
                .filter(ele -> ele.getId().equals("island_" + islandDto.getUuid()))
                .findFirst()
                .orElse(null);
    }

    public static boolean doesCuboidIntersect(ProtectedRegion cuboid) {
        RegionManager regionManager = getRegionManager();

        return regionManager.getApplicableRegions(cuboid).size() != 0;
    }
}
