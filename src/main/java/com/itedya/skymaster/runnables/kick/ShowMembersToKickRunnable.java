package com.itedya.skymaster.runnables.kick;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandMemberDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandMemberDto;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.InventoryUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows GUI with members available to kick from island
 * <p>
 * Executor: ChooseIslandToKickFromGUIHandler
 * <p>
 * Run asynchronously!
 */
public class ShowMembersToKickRunnable extends BukkitRunnable {
    private final Player executor;
    private final int islandId;

    public ShowMembersToKickRunnable(Player executor, int islandId) {
        this.executor = executor;
        this.islandId = islandId;
    }

    private final List<IslandMemberDto> members = new ArrayList<>();

    @Override
    public void run() {
        try {
            Connection connection = Database.getInstance().getConnection();

            var memberDao = new IslandMemberDao(connection);

            members.addAll(memberDao.getByIslandId(islandId));

            ThreadUtil.sync(this::createInventory);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            executor.sendMessage(ChatUtil.getServerErrorMessage());
        }
    }

    /**
     * Creates inventory and displays it.
     * Run synchronously!
     */
    public void createInventory() {
        try {
            var inv = Bukkit.createInventory(null, InventoryUtil.calculateInvSize(members.size()), "Wybierz osobę");

            for (var member : members) {
                var itemStack = InventoryUtil.createItemStack(member);
                var meta = itemStack.getItemMeta();
                var container = meta.getPersistentDataContainer();

                PersistentDataContainerUtil.setString(container, "member-uuid", member.getPlayerUuid());
                PersistentDataContainerUtil.setInt(container, "island-id", islandId);
                PersistentDataContainerUtil.setString(container, "inventory-identifier", "choose-member-to-kick-gui");

                itemStack.setItemMeta(meta);
                inv.addItem(itemStack);
            }

            executor.openInventory(inv);
        } catch (Exception e) {
            e.printStackTrace();
            executor.sendMessage(ChatUtil.getServerErrorMessage());
        }
    }
}
