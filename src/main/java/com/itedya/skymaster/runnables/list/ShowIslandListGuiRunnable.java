package com.itedya.skymaster.runnables.list;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.utils.InventoryUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

// LISTENER - ListUserIslandsGUIHandler
public class ShowIslandListGuiRunnable extends BukkitRunnable {
    private final Player player;
    private final Player playerToCheck;
    private Connection connection;
    private int guiSize;
    private List<IslandDto> userIslands = new ArrayList<>();

    public ShowIslandListGuiRunnable(Player player, Player playerToCheck) {
        this.player = player;
        this.playerToCheck = playerToCheck;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);
            String ownerUuid = playerToCheck.getUniqueId().toString();
            userIslands = islandDao.getByOwnerUuidWithAllRelations(ownerUuid);
            userIslands.addAll(islandDao.getByMemberUuidWithAllRelations(ownerUuid));

            this.connection.close();

            this.guiSize = 9;
            while (guiSize < userIslands.size()) guiSize += 9;

            ThreadUtil.sync(this::assembleInventory);
        } catch (Exception e) {
            try {
                this.connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera!");
        }
    }

    public void assembleInventory() {
        try {
            Inventory inventory = Bukkit.createInventory(null, guiSize,
                    ChatColor.LIGHT_PURPLE + "Wyspy gracza " + playerToCheck.getName());

            for (var island : userIslands) inventory.addItem(InventoryUtil.createItemStack(island));

            var firstItem = inventory.getItem(0);
            if (firstItem != null) {
                var meta = firstItem.getItemMeta();
                var container = meta.getPersistentDataContainer();

                PersistentDataContainerUtil.setString(container, "inventory-identifier", "user-islands-gui");
                PersistentDataContainerUtil.setString(container, "user-uuid", playerToCheck.getUniqueId().toString());
            }

            player.openInventory(inventory);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera!");
        }
    }
}
