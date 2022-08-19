package com.itedya.skymaster.runnables.invite;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.database.IslandInviteDto;
import com.itedya.skymaster.dtos.database.IslandMemberDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.util.Objects;

// Executor - AcceptInviteToIslandSubCommand
// Should be started asynchronously
public class AcceptInviteToIslandRunnable extends SkymasterRunnable {
    private final Player player;
    private final IslandInviteDto dto;
    private Connection connection;
    private int maxAllowedIslands;

    public AcceptInviteToIslandRunnable(Player player, IslandInviteDto dto) {
        super(player, true);
        this.player = player;
        this.dto = dto;
    }

    @Override
    public void run() {
        maxAllowedIslands = PlayerUtil.getMaxAllowedIslands(dto.toPlayer);
        ThreadUtil.async(this::validateMaxAllowedIslands);
    }

    public void validateMaxAllowedIslands() {
        try {
            this.connection = Database.getInstance().getConnection();

            if (IslandUtil.getIslandAmount(connection, dto.toPlayer.getUniqueId().toString()) >= this.maxAllowedIslands) {
                if (player.getUniqueId().toString().equals(dto.toPlayer.getUniqueId().toString())) {
                    player.sendMessage(new ComponentBuilder()
                            .append(ChatUtil.PREFIX + " ")
                            .append("Nie możesz stworzyć lub dołączyć do tylu wysp! ").color(ChatColor.YELLOW)
                            .append("Max: " + maxAllowedIslands).bold(true)
                            .append("!").bold(false)
                            .create());
                } else {
                    player.sendMessage(new ComponentBuilder()
                            .append(ChatUtil.PREFIX + " ")
                            .append("Ten użytkownik nie może stworzyć lub dołączyć do tylu wysp! ").color(ChatColor.YELLOW)
                            .append("Max: " + maxAllowedIslands).bold(true)
                            .append("!").bold(false)
                            .create());
                }

                return;
            }

            ThreadUtil.async(this::createIslandMemberDto);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void createIslandMemberDto() {
        try {
            IslandMemberDto memberDto = new IslandMemberDto();
            memberDto.islandId = dto.islandDto.id;
            memberDto.playerUuid = dto.toPlayer.getUniqueId().toString();

            IslandMemberDao islandMemberDao = new IslandMemberDao(connection);
            islandMemberDao.create(memberDto);

            ThreadUtil.sync(this::commitWorldGuard);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    private void commitWorldGuard() {
        try {
            var region = WorldGuardUtil.getRegionForId(dto.islandDto.id);

            var members = region.getMembers();
            members.addPlayer(dto.toPlayer.getUniqueId());
            region.setMembers(members);

            ThreadUtil.async(this::commitData);
        } catch (Exception e) {
            super.errorHandling(e);
        }
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
                        .append(ChatUtil.PREFIX + " ")
                        .append("Gracz ").color(ChatColor.GREEN)
                        .append(toPlayer.getName()).bold(true)
                        .append(" został dodany do wyspy ").bold(false)
                        .append("\"" + island.name + "\"").bold(true)
                        .append(" gracza ")
                        .append(toPlayer.getName()).bold(true)
                        .create());

                return;
            }

            fromPlayer.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Gracz ").color(ChatColor.GREEN)
                    .append(toPlayer.getName()).bold(true)
                    .append(" został dodany do wyspy").bold(false)
                    .create());

            toPlayer.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Zostałeś dodany do wyspy ").color(ChatColor.GREEN)
                    .append("\"" + island.name + "\"").bold(true)
                    .append(" gracza ").bold(false)
                    .append(toPlayer.getName()).bold(true)
                    .create());

        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
