package com.itedya.skymaster.utils;

import com.fastasyncworldedit.core.FaweAPI;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandMemberDto;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldGuardUtil {
//    public static ProtectedRegion createRegion(String regionName, BlockVector3 from, BlockVector3 to) {
//        ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionName, from, to);
//        RegionManager regionManager = getRegionManager();
//        regionManager.addRegion(protectedRegion);
//        return protectedRegion;
//    }

    public static ProtectedRegion createRegionWithoutSaving(String regionName, BlockVector3 from, BlockVector3 to) {
        return new ProtectedCuboidRegion(regionName, from, to);
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

//    public static ProtectedRegion getRegionForDto(IslandDto islandDto) {
//        return getAllRegions()
//                .values()
//                .stream()
//                .filter(ele -> ele.getId().equals("island_" + islandDto.getId()))
//                .findFirst()
//                .orElse(null);
//    }

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

        return BlockVector3.at(x, 0, z);
    }

    public static BlockVector3 calculateClipboardSpawnPosition(int nth, Clipboard clipboard) {
        var islandPosition = calculateIslandPosition(nth);

        var spawnAtX = islandPosition.getX() + (clipboard.getWidth() / 2.0);
        var spawnAtY = 120;
        var spawnAtZ = islandPosition.getZ() + (clipboard.getLength() / 2.0);

        return BlockVector3.at(spawnAtX, spawnAtY, spawnAtZ);
    }

    public static BlockVector3 calculateIslandHomePosition(int nth) {
        var islandPosition = calculateIslandPosition(nth);
        org.bukkit.World world = Bukkit.getWorld("world_islands");

        double middleY = world.getHighestBlockYAt(islandPosition.getX(), islandPosition.getZ()) + 1;

        return BlockVector3.at(islandPosition.getX(), middleY, islandPosition.getZ());
    }
}
