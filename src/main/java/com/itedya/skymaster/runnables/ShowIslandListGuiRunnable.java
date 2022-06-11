package com.itedya.skymaster.runnables;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.dtos.IslandSchematicDto;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

            for (int i = 0; i < userIslands.size(); i++) {
                IslandDto islandDto = userIslands.get(i);

                IslandSchematicDto schematic = islandDto.getSchematic();
                IslandHomeDto home = islandDto.getHome();

                ItemStack itemStack = new ItemStack(schematic.getMaterial());
                ItemMeta itemMeta = itemStack.getItemMeta();

                if (i == 0) {
                    PersistentDataContainerUtil.setString(
                            itemMeta.getPersistentDataContainer(),
                            "inventory-identifier",
                            "user-islands-gui"
                    );

                    PersistentDataContainerUtil.setString(
                            itemMeta.getPersistentDataContainer(),
                            "user-uuid",
                            playerToCheck.getUniqueId().toString()
                    );
                }

                PersistentDataContainerUtil.setInt(
                        itemMeta.getPersistentDataContainer(),
                        "island-id",
                        islandDto.getId()
                );

                itemMeta.setDisplayName("Wyspa " + islandDto.getName());

                itemMeta.setLore(List.of(
                        ChatColor.YELLOW + "X: " + home.getX(),
                        ChatColor.YELLOW + "Z: " + home.getZ()
                ));

                PersistentDataContainerUtil.setInt(itemMeta.getPersistentDataContainer(), "island-id", islandDto.getId());

                itemStack.setItemMeta(itemMeta);

                inventory.addItem(itemStack);
            }

            player.openInventory(inventory);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera!");
        }
    }
}
