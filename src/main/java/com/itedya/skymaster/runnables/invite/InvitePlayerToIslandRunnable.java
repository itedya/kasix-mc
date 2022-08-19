package com.itedya.skymaster.runnables.invite;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandInviteDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.PlayerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class InvitePlayerToIslandRunnable extends SkymasterRunnable {
    private final int islandId;
    private final Player executor;
    private final Player islandOwner;
    private final Player inviteToPlayer;
    private IslandDto islandDto;
    private IslandInviteDto inviteDto;

    /**
     * Sends island member invite offer to player
     * Run asynchronously!
     *
     * @param executor       Executor of command
     * @param islandOwner    Owner of island that member is invited to
     * @param inviteToPlayer Player that is invited to island
     * @param islandId       Island id
     */
    public InvitePlayerToIslandRunnable(Player executor, Player islandOwner, Player inviteToPlayer, int islandId) {
        super(executor, true);
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

            var maxMembers = PlayerUtil.getMaxAllowedIslandMembers(inviteToPlayer);
            if (islandMemberDao.getByIslandId(islandId).size() >= maxMembers) {
                executor.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Możesz dodać maksymalnie ").color(ChatColor.YELLOW)
                        .append(maxMembers + "").bold(true)
                        .append(" członków do wyspy!").bold(false)
                        .create());
                super.closeDatabase();
                return;
            }

            if (islandMemberDao.isMember(inviteToPlayer.getUniqueId().toString(), islandId)) {
                executor.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Ten gracz już jest członkiem tej wyspy.").color(ChatColor.YELLOW)
                        .create());
                super.closeDatabase();
                return;
            }

            IslandDao islandDao = new IslandDao(connection);

            islandDto = islandDao.getById(islandId);

            inviteDto = new IslandInviteDto();
            inviteDto.islandDto = islandDto;
            inviteDto.fromPlayer = this.islandOwner;
            inviteDto.toPlayer = this.inviteToPlayer;
            inviteDto.ttl = 60;

            ThreadUtil.sync(this::finish);
        } catch (Exception e) {
            super.errorHandling(e);
        }

    }

    private void finish() {
        try {
            IslandInviteDao islandInviteDao = IslandInviteDao.getInstance();

            islandInviteDao.addToQueue(inviteDto);

            executor.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Zaproszono gracza ").color(ChatColor.GREEN)
                    .append(inviteToPlayer.getName()).bold(true)
                    .append(" do wyspy ").bold(false)
                    .append("\"" + islandDto.name + "\"").bold(true)
                    .create());

            TextComponent acceptButton = new TextComponent("[AKCEPTUJ]");
            acceptButton.setBold(true);
            acceptButton.setColor(ChatColor.GREEN);
            acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wyspa akceptuj " + inviteDto.fromPlayer.getName()));

            inviteToPlayer.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Dostałeś zaproszenie do wyspy ").color(ChatColor.GREEN)
                    .append("\"" + islandDto.name + "\"").bold(true)
                    .append(" od gracza ").bold(false)
                    .append(islandOwner.getName()).bold(true)
                    .append(" ")
                    .append(acceptButton)
                    .create());
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
