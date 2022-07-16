package com.itedya.skymaster.runnables.island;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandHomeDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandHomeDto;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ShowIslandGuiRunnable extends BukkitRunnable {
    private final Player player;
    private final Integer islandId;
    private Connection connection;
    private IslandDto islandDto;
    private IslandHomeDto islandHomeDto;

    public ShowIslandGuiRunnable(Player player, Integer islandId) {
        this.player = player;
        this.islandId = islandId;
    }

    @Override
    public void run() {
        try {
            // get database connection
            connection = Database.getInstance().getConnection();

            // fetch island by id
            IslandDao islandDao = new IslandDao(connection);
            IslandHomeDao islandHomeDao = new IslandHomeDao(connection);

            this.islandDto = islandDao.getById(islandId);
            this.islandHomeDto = islandHomeDao.firstByIslandId(islandId);

            ThreadUtil.sync(this::createInventory);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
        }
    }

    private void createInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "Szczegóły wyspy");
        inventory.addItem(getTeleportToIslandItem());

        String playerUuid = player.getUniqueId().toString();

        if ((playerUuid.equals(islandDto.getOwnerUuid()) && player.hasPermission("skymaster.islands.remove")) ||
                !player.getUniqueId().toString().equals(islandDto.getOwnerUuid()) && player.hasPermission("skymaster.islands.remove-someone")) {
            inventory.addItem(getRemoveItem());
        }

        if ((player.getUniqueId().toString().equals(islandDto.getOwnerUuid()) && player.hasPermission("skymaster.islands.reset-permissions")) ||
                !player.getUniqueId().toString().equals(islandDto.getOwnerUuid()) && player.hasPermission("skymaster.islands.reset-permissions-someone")) {
            inventory.addItem(getResetWorldGuardItem());
        }

        player.openInventory(inventory);
    }

    private ItemStack getTeleportToIslandItem() {
        ItemStack item = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Teleportuj do domu wyspy");
        itemMeta.setLore(List.of(
                ChatColor.YELLOW + "X: " + islandHomeDto.getX(),
                ChatColor.YELLOW + "Z: " + islandHomeDto.getZ()
        ));
        PersistentDataContainerUtil.setInt(itemMeta.getPersistentDataContainer(), "island-id", islandDto.getId());
        PersistentDataContainerUtil.setString(itemMeta.getPersistentDataContainer(), "inventory-identifier", "island-info-gui");
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack getRemoveItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Usuń wyspę");
        PersistentDataContainerUtil.setInt(itemMeta.getPersistentDataContainer(), "island-id", islandDto.getId());
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack getResetWorldGuardItem() {
        ItemStack item = new ItemStack(Material.REPEATING_COMMAND_BLOCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Zresetuj permisje WorldGuard");
        itemMeta.setLore(List.of(
                ChatColor.YELLOW + "Nie działa ci coś na wyspie? Nie możesz budować?",
                ChatColor.YELLOW + "Ktoś ma permisje do robienia czegoś mimo iż nie powinien?",
                ChatColor.GOLD + "Ten przycisk służy do naprawy takich rzeczy!"
        ));
        PersistentDataContainerUtil.setInt(itemMeta.getPersistentDataContainer(), "island-id", islandDto.getId());
        item.setItemMeta(itemMeta);
        return item;
    }

    public void shutdown() {
        if (connection != null) {
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
