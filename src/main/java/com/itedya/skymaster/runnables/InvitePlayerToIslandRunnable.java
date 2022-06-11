package com.itedya.skymaster.runnables;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandInviteDto;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;

public class InvitePlayerToIslandRunnable implements Runnable {
    private final int islandId;
    private final Player player;
    private final Player inviteToPlayer;
    private Connection connection;

    public InvitePlayerToIslandRunnable(int islandId, Player player, Player inviteToPlayer) {
        this.islandId = islandId;
        this.player = player;
        this.inviteToPlayer = inviteToPlayer;
    }

    @Override
    public void run() {
        try {
            connection = Database.getInstance().getConnection();
            IslandDao islandDao = new IslandDao(connection);

            IslandDto islandDto = islandDao.getById(islandId);

            IslandInviteDto islandInviteDto = new IslandInviteDto();
            islandInviteDto.setIslandDto(islandDto);
            islandInviteDto.setFromPlayer(player);
            islandInviteDto.setToPlayer(inviteToPlayer);

            ThreadUtil.sync(() -> {
                IslandInviteDao islandInviteDao = IslandInviteDao.getInstance();

                if (islandInviteDao.doesPlayerHaveInvite(inviteToPlayer.getUniqueId().toString())) {
                    islandInviteDao.addToQueue(islandInviteDto);
                    player.sendMessage(ChatColor.GREEN + "Zaproszono gracza " + inviteToPlayer.getName() + " do wyspy " + islandDto.getName());
                    inviteToPlayer.sendMessage(ChatColor.GREEN + "Dostałeś zaproszenie do wyspy " +
                            islandDto.getName() + " od gracza " + player.getName());
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Ten gracz jest już zaproszony, poczekaj do 60 sekund.");
                }
            });

            this.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            this.shutdown();
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
