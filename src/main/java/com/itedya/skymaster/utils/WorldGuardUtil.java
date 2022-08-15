package com.itedya.skymaster.utils;

import com.fastasyncworldedit.core.FaweAPI;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandMemberDto;
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
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldGuardUtil {
    public static ProtectedRegion createRegionWithoutSaving(String regionName, BlockVector3 from, BlockVector3 to) {
        return new ProtectedCuboidRegion(regionName, from, to);
    }

    public static ProtectedRegion getRegionForLocation(Location location) {
        var vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        var regionManager = WorldGuardUtil.getRegionManager();
        var applicableRegions = regionManager.getApplicableRegions(vector);

        ProtectedRegion islandRegion = null;

        for (var region : applicableRegions) {
            if (region.getId().startsWith("island_")) {
                islandRegion = region;
                break;
            }
        }

        return islandRegion;
    }

    public static ProtectedRegion resetRegionFlags(ProtectedRegion protectedRegion) {
        protectedRegion.setFlags(new HashMap<>());
        protectedRegion.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
        return protectedRegion;
    }

    public static ProtectedRegion resetRegionMembers(ProtectedRegion protectedRegion, IslandDto islandDto, List<IslandMemberDto> islandMembers) {
        DefaultDomain members = protectedRegion.getMembers();

        members.removeAll();

        members.addPlayer(UUID.fromString(islandDto.ownerUuid));
        islandMembers.forEach(ele -> members.addPlayer(UUID.fromString(ele.playerUuid)));

        protectedRegion.setMembers(members);
        return protectedRegion;
    }

    public static ProtectedRegion removeRegionMemberByUuid(ProtectedRegion protectedRegion, UUID uuid) {
        DefaultDomain members = protectedRegion.getMembers();

        members.removePlayer(uuid);

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

    public static void removeRegionForId(Integer islandId) {
        RegionManager manager = getRegionManager();
        ProtectedRegion protectedRegion = getRegionForId(islandId);
        manager.removeRegion(protectedRegion.getId());
    }

    public static Map<String, ProtectedRegion> getAllRegions() {
        RegionManager regionManager = getRegionManager();
        return regionManager.getRegions();
    }

    public static ProtectedRegion getRegionForId(Integer islandId) {
        return getAllRegions()
                .values()
                .stream()
                .filter(ele -> ele.getId().equals("island_" + islandId))
                .findFirst()
                .orElse(null);
    }

    public static boolean doesCuboidIntersect(ProtectedRegion cuboid) {
        RegionManager regionManager = getRegionManager();

        return regionManager.getApplicableRegions(cuboid).size() != 0;
    }

    public static BlockVector3 calculateIslandPosition(int nth) {
        var x = nth * 1000 + nth * 1000 - 1000 - 500;
        var z = 500;

        return BlockVector3.at(x, 120, z);
    }
}
