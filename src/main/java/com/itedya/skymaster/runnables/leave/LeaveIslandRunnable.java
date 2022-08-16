package com.itedya.skymaster.runnables.leave;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LeaveIslandRunnable extends SkymasterRunnable {
    private final int islandId;
    private final Player executor;
    private IslandDto islandDto;

    public LeaveIslandRunnable(Player player, int islandId) {
        super(player, true);

        this.executor = player;
        this.islandId = islandId;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            String executorUuid = executor.getUniqueId().toString();

            IslandMemberDao islandMemberDao = new IslandMemberDao(connection);
            boolean isMember = islandMemberDao.isMember(executorUuid, islandId);

            if (!isMember) {
                executor.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Nie możesz wyjść z tej wyspy, ponieważ do niej nie należysz!").color(ChatColor.RED)
                        .create());
                return;
            }

            islandMemberDao.remove(executorUuid, islandId);

            connection.commit();

            IslandDao islandDao = new IslandDao(connection);
            islandDto = islandDao.getById(islandId);

            connection.close();

            ThreadUtil.sync(this::alert);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void alert() {
        Player owner = Bukkit.getPlayer(UUID.fromString(islandDto.ownerUuid));
        if (owner == null) {
            executor.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Wyszedłeś z wyspy \"").bold(false).color(ChatColor.GREEN)
                    .append(islandDto.name).bold(true)
                    .append("\".").bold(false)
                    .create());
            return;
        }

        executor.sendMessage(new ComponentBuilder()
                .append(ChatUtil.PREFIX + " ")
                .append("Wyszedłeś z wyspy \"").bold(false).color(ChatColor.GREEN)
                .append(islandDto.name).bold(true)
                .append("\" gracza ").bold(false)
                .append(owner.getName()).bold(true)
                .append(".").bold(false)
                .create());
    }
}
