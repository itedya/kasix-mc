package com.itedya.skymaster.runnables.invite;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandMemberDto;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.UUID;

public class AddPlayerToIslandRunnable extends BukkitRunnable {
    private final int islandId;
    private final Player executor;
    private final OfflinePlayer inviteToPlayer;
    private Connection connection;
    private IslandDto islandDto;

    /**
     * Sends island member invite offer to player
     * Run asynchronously!
     *
     * @param executor       Executor of command
     * @param inviteToPlayer Player that is invited to island
     * @param islandId       Island id
     */
    public AddPlayerToIslandRunnable(Player executor, OfflinePlayer inviteToPlayer, int islandId) {
        this.executor = executor;
        this.islandId = islandId;
        this.inviteToPlayer = inviteToPlayer;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            var islandDao = new IslandDao(connection);
            islandDto = islandDao.getById(islandId);

            IslandMemberDto memberDto = new IslandMemberDto();
            memberDto.setIslandId(islandId);
            memberDto.setPlayerUuid(inviteToPlayer.getUniqueId().toString());

            IslandMemberDao islandMemberDao = new IslandMemberDao(connection);
            islandMemberDao.create(memberDto);

            ThreadUtil.sync(this::commitWorldGuard);
        } catch (Exception e) {
            e.printStackTrace();
            executor.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }
    }

    private void commitWorldGuard() {
        var region = WorldGuardUtil.getRegionForId(islandId);

        var members = region.getMembers();
        members.addPlayer(inviteToPlayer.getUniqueId());
        region.setMembers(members);

        ThreadUtil.async(this::commitData);
    }

    private void commitData() {
        try {
            connection.commit();
            connection.close();

            ThreadUtil.sync(this::announce);
        } catch (Exception e) {
            e.printStackTrace();
            executor.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }
    }

    private void announce() {
        OfflinePlayer islandOwner = Bukkit.getOfflinePlayer(UUID.fromString(islandDto.getOwnerUuid()));

        executor.sendMessage(new ComponentBuilder()
                .color(ChatColor.GREEN)
                .append("Gracz ")
                .append(inviteToPlayer.getName()).bold(true)
                .append(" został dodany do wyspy ").bold(false)
                .append("\"" + islandDto.getName() + "\"").bold(true)
                .append(" gracza ").bold(false)
                .append(islandOwner.getName()).bold(true)
                .create());
    }

    private void shutdown() {
        try {
            if (connection != null) {
                connection.rollback();
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
