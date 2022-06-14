package com.itedya.skymaster.runnables.invite;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.utils.InventoryUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.List;

public class ShowIslandsForInvitesGuiRunnable extends BukkitRunnable {
    private Connection connection;
    private final Player player;
    private List<IslandDto> userIslands;
    private final Player invitedPlayer;

    public ShowIslandsForInvitesGuiRunnable(Player player, Player invitedPlayer) {
        this.player = player;
        this.invitedPlayer = invitedPlayer;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);

            this.userIslands = islandDao.getByOwnerUuidWithAllRelations(player.getUniqueId().toString());

            ThreadUtil.sync(this::generateInventory);

            this.connection.close();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
        }
    }

    public void generateInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, "Wybierz wyspę do której chcesz zaprosić");

        for (IslandDto island : userIslands) {
            inventory.addItem(InventoryUtil.createItemStack(island));
        }

        var firstItem = inventory.getItem(0);
        if (firstItem != null) {
            var meta = firstItem.getItemMeta();
            var container = meta.getPersistentDataContainer();

            PersistentDataContainerUtil.setString(container, "inventory-identifier", "choose-island-invite-member-gui");
            PersistentDataContainerUtil.setString(container, "invite-to-player-uuid", invitedPlayer.getUniqueId().toString());
        }

        player.openInventory(inventory);
    }
}
