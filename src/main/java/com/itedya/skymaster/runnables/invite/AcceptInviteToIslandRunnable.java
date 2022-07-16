package com.itedya.skymaster.runnables.invite;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.database.IslandInviteDto;
import com.itedya.skymaster.dtos.database.IslandMemberDto;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.Objects;

// Executor - AcceptInviteToIslandSubCommand
// Should be started asynchronously
public class AcceptInviteToIslandRunnable extends BukkitRunnable {
    private final Player player;
    private final IslandInviteDto dto;
    private Connection connection;

    public AcceptInviteToIslandRunnable(Player player, IslandInviteDto dto) {
        this.player = player;
        this.dto = dto;
    }

    @Override
    public void run() {
        var island = dto.islandDto;
        var toPlayer = dto.toPlayer;

        try {
            this.connection = Database.getInstance().getConnection();

            IslandMemberDto memberDto = new IslandMemberDto();
            memberDto.islandId = island.id;
            memberDto.playerUuid = toPlayer.getUniqueId().toString();

            IslandMemberDao islandMemberDao = new IslandMemberDao(connection);
            islandMemberDao.create(memberDto);

            ThreadUtil.sync(this::commitWorldGuard);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }
    }

    private void commitWorldGuard() {
        var region = WorldGuardUtil.getRegionForId(dto.islandDto.id);

        var members = region.getMembers();
        members.addPlayer(dto.toPlayer.getUniqueId());
        region.setMembers(members);

        ThreadUtil.async(this::commitData);
    }

    private void commitData() {
        try {
            var island = dto.islandDto;
            var fromPlayer = dto.fromPlayer;
            var toPlayer = dto.toPlayer;

            connection.commit();
            connection.close();

            if (!Objects.equals(toPlayer.getUniqueId().toString(), player.getUniqueId().toString())) {
                player.sendMessage(new ComponentBuilder()
                        .color(ChatColor.GREEN)
                        .append("Gracz ")
                        .append(toPlayer.getName()).bold(true)
                        .append(" został dodany do wyspy ").bold(false)
                        .append("\"" + island.name + "\"").bold(true)
                        .append(" gracza ")
                        .append(toPlayer.getName()).bold(true)
                        .create());

                return;
            }

            fromPlayer.sendMessage(new ComponentBuilder()
                    .color(ChatColor.GREEN)
                    .append("Gracz ")
                    .append(toPlayer.getName()).bold(true)
                    .append(" został dodany do wyspy").bold(false)
                    .create());

            toPlayer.sendMessage(new ComponentBuilder()
                    .color(ChatColor.GREEN)
                    .append("Zostałeś dodany do wyspy ")
                    .append("\"" + island.name + "\"").bold(true)
                    .append(" gracza ").bold(false)
                    .append(toPlayer.getName()).bold(true)
                    .create());

        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }
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
