package com.itedya.skymaster.runnables.invite;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.utils.InventoryUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.List;

public class ShowIslandsForInvitesGuiRunnable extends BukkitRunnable {
    private Connection connection;
    private List<IslandDto> userIslands;
    private final Player executor;
    private final OfflinePlayer invitedPlayer;
    private final OfflinePlayer islandOwner;
    private final Boolean withAccept;

    /**
     * Shows islands to which executor can add members
     * Run asynchronously!
     *
     * @param executor      Executor of command
     * @param islandOwner   Owner of islands to display in GUI
     * @param invitedPlayer Player that executor is inviting
     */
    public ShowIslandsForInvitesGuiRunnable(Player executor, OfflinePlayer islandOwner, OfflinePlayer invitedPlayer, Boolean withAccept) {
        this.executor = executor;
        this.islandOwner = islandOwner;
        this.invitedPlayer = invitedPlayer;
        this.withAccept = withAccept;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);
            this.userIslands = islandDao.getByOwnerUuidWithAllRelations(islandOwner.getUniqueId().toString());

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
            executor.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
        }
    }

    public void generateInventory() {
        Inventory inventory = Bukkit.createInventory(null, InventoryUtil.calculateInvSize(userIslands.size()), "Wybierz wyspę do której chcesz zaprosić");

        for (IslandDto island : userIslands) {
            ItemStack itemStack = InventoryUtil.createItemStack(island);

            var meta = itemStack.getItemMeta();
            var container = meta.getPersistentDataContainer();

            PersistentDataContainerUtil.setString(container, "inventory-identifier", "choose-island-invite-member-gui");
            PersistentDataContainerUtil.setString(container, "island-owner-uuid", islandOwner.getUniqueId().toString());
            PersistentDataContainerUtil.setString(container, "invite-to-player-uuid", invitedPlayer.getUniqueId().toString());
            if (this.withAccept) {
                PersistentDataContainerUtil.setInt(container, "with-accept", 1);
            } else {
                PersistentDataContainerUtil.setInt(container, "with-accept", 0);
            }

            itemStack.setItemMeta(meta);

            inventory.addItem(itemStack);
        }

        executor.openInventory(inventory);
    }
}
