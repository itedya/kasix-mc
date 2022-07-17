package com.itedya.skymaster.runnables.kick;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.UUID;

/**
 * Kicks member from island
 * <p>
 * Run asyncronously!
 */
public class KickPlayerFromIslandRunnable extends BukkitRunnable {
    private final int islandId;
    private final String memberUuid;
    private final Player executor;

    public KickPlayerFromIslandRunnable(Player executor, int islandId, String memberUuid) {
        this.islandId = islandId;
        this.memberUuid = memberUuid;
        this.executor = executor;
    }

    private Connection connection;

    @Override
    public void run() {
        try {
            connection = Database.getInstance().getConnection();

            var memberDao = new IslandMemberDao(connection);

            if (!memberDao.isMember(memberUuid, islandId)) {
                executor.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Ten gracz nie jest członkiem tej wyspy, nie może on zostać usunięty!");
                return;
            }

            int res = memberDao.remove(memberUuid, islandId);

            if (res == 0) {
                throw new Exception("Affected 0 rows in DB, something weird is happening | memberUuid - " + memberUuid + " | islandId - " + islandId);
            } else if (res > 1) {
                throw new Exception("Affected " + res + " rows in DB, something weird is happening | memberUuid - " + memberUuid + " | islandId - " + islandId);
            }

            ThreadUtil.sync(this::removeUserInWorldGuard);
        } catch (Exception e) {
            this.shutdown();
            e.printStackTrace();
            executor.sendMessage(ChatUtil.SERVER_ERROR);
        }
    }

    /**
     * Removes user in WorldGuard
     * Use synchronously!
     */
    public void removeUserInWorldGuard() {
        try {
            var region = WorldGuardUtil.getRegionForId(islandId);
            WorldGuardUtil.removeRegionMemberByUuid(region, UUID.fromString(memberUuid));
            ThreadUtil.async(this::commitToDatabase);
        } catch (Exception e) {
            ThreadUtil.async(this::shutdown);
            e.printStackTrace();
            executor.sendMessage(ChatUtil.SERVER_ERROR);
        }
    }

    /**
     * Commits to database
     * Use asynchronously!
     */
    public void commitToDatabase() {
        try {
            connection.commit();
            connection.close();

            ThreadUtil.sync(this::alert);
        } catch (Exception e) {
            this.shutdown();
            e.printStackTrace();
            executor.sendMessage(ChatUtil.SERVER_ERROR);
        }
    }

    public void alert() {
        try {
            var offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(memberUuid));

            executor.sendMessage(new ComponentBuilder()
                    .color(ChatColor.GREEN)
                    .append("Pomyślnie wyrzucono użytkownika ")
                    .append(offlinePlayer.getName()).bold(true)
                    .append("!").bold(false)
                    .create());
        } catch (Exception e) {
            e.printStackTrace();
            executor.sendMessage(ChatUtil.SERVER_ERROR);
        }
    }

    /**
     * Error handling function
     * <p>
     * Use asynchronously!
     */
    public void shutdown() {
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
