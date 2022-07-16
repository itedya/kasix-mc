package com.itedya.skymaster.runnables.invite;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandInviteDto;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.sql.Connection;

public class InvitePlayerToIslandRunnable implements Runnable {
    private final int islandId;
    private final Player executor;
    private final Player islandOwner;
    private final Player inviteToPlayer;
    private Connection connection;
    private IslandDto islandDto;
    private IslandInviteDto inviteDto;

    /**
     * Sends island member invite offer to player
     * Run asynchronously!
     *
     * @param executor Executor of command
     * @param islandOwner Owner of island that member is invited to
     * @param inviteToPlayer Player that is invited to island
     * @param islandId Island id
     */
    public InvitePlayerToIslandRunnable(Player executor, Player islandOwner, Player inviteToPlayer, int islandId) {
        this.executor = executor;
        this.islandId = islandId;
        this.islandOwner = islandOwner;
        this.inviteToPlayer = inviteToPlayer;
    }

    @Override
    public void run() {
        try {
            connection = Database.getInstance().getConnection();

            IslandMemberDao islandMemberDao = new IslandMemberDao(connection);

            if (islandMemberDao.isMember(inviteToPlayer.getUniqueId().toString(), islandId)) {
                executor.sendMessage(new ComponentBuilder()
                        .append("Ten gracz już jest członkiem tej wyspy.").color(ChatColor.YELLOW)
                        .create());
                this.shutdown();
                return;
            }

            IslandDao islandDao = new IslandDao(connection);

            islandDto = islandDao.getById(islandId);

            inviteDto = new IslandInviteDto();
            inviteDto.setIslandDto(islandDto);
            inviteDto.setFromPlayer(this.islandOwner);
            inviteDto.setToPlayer(this.inviteToPlayer);

            ThreadUtil.sync(this::finish);
        } catch (Exception e) {
            e.printStackTrace();
            executor.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }

    }

    private void finish() {
        IslandInviteDao islandInviteDao = IslandInviteDao.getInstance();

        if (!islandInviteDao.doesPlayerHaveInvite(inviteToPlayer.getUniqueId().toString())) {
            islandInviteDao.addToQueue(inviteDto);

            executor.sendMessage(new ComponentBuilder()
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
                    .append(islandOwner.getName()).bold(true)
                    .create());

        } else {
            executor.sendMessage(ChatColor.YELLOW + "Ten gracz jest już zaproszony, poczekaj do 60 sekund.");
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
