package com.itedya.skymaster.runnables.block;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.daos.VisitBlockDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.VisitBlockDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.itedya.skymaster.runnables.kick.KickPlayerFromIslandRunnable;

import java.util.List;

public class BlockPlayerFromVisitIslandRunnable extends SkymasterRunnable {
    private final Player executor;
    private final OfflinePlayer userToBlock;
    private final Player islandOwner;

    public BlockPlayerFromVisitIslandRunnable(Player executor, OfflinePlayer userToBlock) {
        super(executor, false);
        this.executor = executor;
        this.islandOwner = executor;
        this.userToBlock = userToBlock;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();
            var dao = new IslandDao(connection);
            var memberDao = new IslandMemberDao(connection);
            String uuid = userToBlock.getUniqueId().toString();
            var rawIslands = dao.getByOwnerUuidWithAllRelations(uuid);
            //  Get all islands -> For each island: if blocked user is islands contributor - KickPlayerFromIsland
            for (var island : rawIslands) {
                if (memberDao.isMember(uuid, island.id))
                    ThreadUtil.async(new KickPlayerFromIslandRunnable(islandOwner, island.id, uuid));
            }
            ThreadUtil.async(this::blockPlayerFromVisitIsland);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void blockPlayerFromVisitIsland() {
        try {
            //set block to DB, new checker in VisitIslandSubCommand
            VisitBlockDao visitBlockDao = new VisitBlockDao(connection);
            var visitBlockDto = visitBlockDao.get(islandOwner.getUniqueId().toString(), userToBlock.getUniqueId().toString());
            if (visitBlockDto != null) {
                islandOwner.sendMessage(ChatColor.YELLOW + userToBlock.getName() + " jest już zablokowany");
            } else {
                visitBlockDto = new VisitBlockDto();
                visitBlockDto.blockedPlayerUuid = userToBlock.getUniqueId().toString();
                visitBlockDto.islandOwnerUuid = islandOwner.getUniqueId().toString();
                visitBlockDao.create(visitBlockDto);
                connection.commit();
                ThreadUtil.sync(this::teleportToSpawn);
            }
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    private Player userToTeleport;
    private int islandId;

    /**
     * Teleport user to spawn if he is on island
     */
    public void teleportToSpawn() {
        try {
            if (!userToBlock.isOnline()) return;
            userToTeleport = Bukkit.getPlayer(userToBlock.getUniqueId());

            ProtectedRegion protectedRegion = WorldGuardUtil.getRegionForLocation(userToTeleport.getLocation());
            String protectedRegionId = protectedRegion.getId();

            if (!protectedRegionId.startsWith("island_")) return;

            islandId = Integer.parseInt(protectedRegionId.replaceAll("island_", ""));
            ThreadUtil.async(this::getByOwnerUuid);
        } catch (Exception e) {
            e.printStackTrace();
            executor.sendMessage(ChatUtil.PREFIX + " " + ChatUtil.SERVER_ERROR);
        }
    }

    public void getByOwnerUuid() {
        try {
            IslandDao islandDao = new IslandDao(connection);
            List<IslandDto> islands = islandDao.getByOwnerUuid(islandOwner.getUniqueId().toString());
            connection.commit();
            connection.close();

            for (IslandDto island : islands) {
                if (island.id == islandId) {
                    ThreadUtil.sync(new TeleportUserToSpawn(userToTeleport.getName()));
                    break;
                }
            }

            ThreadUtil.sync(new AlertUsers(executor, islandOwner, userToBlock));
        } catch (Exception e) {
            e.printStackTrace();
            executor.sendMessage(ChatUtil.PREFIX + " " + ChatUtil.SERVER_ERROR);
        }
    }
}

class AlertUsers extends BukkitRunnable {
    private final Player executor;
    private final OfflinePlayer islandOwner;
    private final OfflinePlayer playerToBlock;

    public AlertUsers(Player executor, OfflinePlayer islandOwner, OfflinePlayer playerToBlock) {
        this.executor = executor;
        this.islandOwner = islandOwner;
        this.playerToBlock = playerToBlock;
    }

    @Override
    public void run() {
        if (executor.getUniqueId().equals(islandOwner.getUniqueId())) {
            executor.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX)
                    .append(" ")
                    .append("Gracz ").color(ChatColor.GREEN)
                    .append(playerToBlock.getName()).bold(true)
                    .append(" został zablokowany i nie może teraz wchodzić na twoje wyspy.").bold(false)
                    .create());
        } else {
            executor.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX)
                    .append(" ")
                    .append("Gracz ").color(ChatColor.GREEN)
                    .append(playerToBlock.getName()).bold(true)
                    .append(" został zablokowany i nie może teraz wchodzić na wyspy gracza ").bold(false)
                    .append(islandOwner.getName()).bold(true)
                    .create());

            if (islandOwner.isOnline()) {
                Player player = islandOwner.getPlayer();
                player.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX)
                        .append(" ")
                        .append("Gracz ").color(ChatColor.GREEN)
                        .append(playerToBlock.getName()).bold(true)
                        .append(" został zablokowany i nie może teraz wchodzić na twoje wyspy.").bold(false)
                        .create());
            }
        }

        if (playerToBlock.isOnline()) {
            Player player = playerToBlock.getPlayer();
            player.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX)
                    .append(" ")
                    .append("Zostałeś zablokowany na wyspach gracza ").color(ChatColor.YELLOW)
                    .append(player.getName()).bold(true)
                    .create());
        }
    }
}

class TeleportUserToSpawn extends BukkitRunnable {
    private final String userToTeleportUsername;

    public TeleportUserToSpawn(String userToTeleportUsername) {
        this.userToTeleportUsername = userToTeleportUsername;
    }

    @Override
    public void run() {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + userToTeleportUsername);
    }
}

