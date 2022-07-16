package com.itedya.skymaster.runnables.kick;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.InventoryUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows islands where you can kick members
 * <p>
 * Executor: KickIslandMemberSubCommand
 * <p>
 * Run asynchronously!
 */
public class ShowIslandsForKickRunnable extends BukkitRunnable {
    private final Player executor;
    private final OfflinePlayer player;

    public ShowIslandsForKickRunnable(Player executor, OfflinePlayer player) {
        this.executor = executor;
        this.player = player;
    }

    private final List<IslandDto> islands = new ArrayList<>();

    @Override
    public void run() {
        try {
            Connection connection = Database.getInstance().getConnection();

            var dao = new IslandDao(connection);
            var memberDao = new IslandMemberDao(connection);

            var rawIslands = dao.getByOwnerUuidWithAllRelations(player.getUniqueId().toString());

            // filter islands based on amount of members
            for (var island : rawIslands) {
                var members = memberDao.getByIslandId(island.getId());

                if (members.size() > 0) {
                    island.setMembers(members);
                    islands.add(island);
                }
            }

            ThreadUtil.sync(this::createInventory);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            executor.sendMessage(ChatUtil.getServerErrorMessage());
        }
    }

    /**
     * Creates inventory and displays it, run synchronously
     */
    public void createInventory() {
        var inv = Bukkit.createInventory(null, InventoryUtil.calculateInvSize(islands.size()), "Wybierz wyspę");

        for (var island : islands) {
            inv.addItem(InventoryUtil.createItemStack(island));
        }

        var firstItem = inv.getItem(0);
        if (firstItem != null) {
            var meta = firstItem.getItemMeta();
            var container = meta.getPersistentDataContainer();

            PersistentDataContainerUtil.setString(container, "inventory-identifier", "choose-island-to-kick-member-gui");
            firstItem.setItemMeta(meta);
            inv.setItem(0, firstItem);
        }

        executor.openInventory(inv);
    }
}
