package com.itedya.skymaster.runnables.invite;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandInviteDto;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.sql.Connection;

public class InvitePlayerToIslandRunnable implements Runnable {
    private final int islandId;
    private final Player player;
    private final Player inviteToPlayer;
    private Connection connection;
    private IslandDto islandDto;
    private IslandInviteDto inviteDto;

    public InvitePlayerToIslandRunnable(int islandId, Player player, Player inviteToPlayer) {
        this.islandId = islandId;
        this.player = player;
        this.inviteToPlayer = inviteToPlayer;
    }

    @Override
    public void run() {
        try {
            connection = Database.getInstance().getConnection();

            IslandMemberDao islandMemberDao = new IslandMemberDao(connection);

            if (islandMemberDao.isMember(inviteToPlayer.getUniqueId().toString(), islandId)) {
                player.sendMessage(new ComponentBuilder()
                        .append("Ten gracz już jest członkiem tej wyspy.").color(ChatColor.YELLOW)
                        .create());
                this.shutdown();
                return;
            }

            IslandDao islandDao = new IslandDao(connection);

            islandDto = islandDao.getById(islandId);

            inviteDto = new IslandInviteDto();
            inviteDto.setIslandDto(islandDto);
            inviteDto.setFromPlayer(player);
            inviteDto.setToPlayer(inviteToPlayer);

            ThreadUtil.async(this::finish);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }

    }

    private void finish() {
        IslandInviteDao islandInviteDao = IslandInviteDao.getInstance();

        if (!islandInviteDao.doesPlayerHaveInvite(inviteToPlayer.getUniqueId().toString())) {
            islandInviteDao.addToQueue(inviteDto);

            player.sendMessage(new ComponentBuilder()
                    .color(net.md_5.bungee.api.ChatColor.GREEN)
                    .append("Zaproszono gracza ")
                    .append(inviteToPlayer.getName()).bold(true)
                    .append(" do wyspy ").bold(false)
                    .append("\"" + islandDto.getName() + "\"").bold(true)
                    .create());

            inviteToPlayer.sendMessage(new ComponentBuilder()
                    .color(net.md_5.bungee.api.ChatColor.GREEN)
                    .append("Dostałeś zaproszenie do wyspy ")
                    .append("\"" + islandDto.getName() + "\"").bold(true)
                    .append(" od gracza ").bold(false)
                    .append(player.getName()).bold(true)
                    .create());

        } else {
            player.sendMessage(ChatColor.YELLOW + "Ten gracz jest już zaproszony, poczekaj do 60 sekund.");
        }
    }

    public void shutdown() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
