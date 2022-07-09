package com.itedya.skymaster.runnables.view;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.ViewBlockDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.InventoryUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows ViewIsland GUI
 * <p>
 * Run asynchronously
 */
public class ShowViewIslandGUIRunnable extends SkymasterRunnable {
    public ShowViewIslandGUIRunnable(Player executor, OfflinePlayer owner) {
        super(executor, true);

        data.put("executor", executor);
        data.put("owner", owner);
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            Player executor = (Player) data.get("executor");
            OfflinePlayer owner = (OfflinePlayer) data.get("owner");

            IslandDao islandDao = new IslandDao(connection);
            var islands = islandDao.getByOwnerUuidWithAllRelations(owner.getUniqueId().toString());
            var filteredIslands = new ArrayList<>();

            ViewBlockDao blockDao = new ViewBlockDao(connection);

            for (IslandDto island : islands) {
                if (blockDao.get(island.getId(), executor.getUniqueId().toString()) == null) {
                    filteredIslands.add(island);
                }
            }

            data.put("islands", filteredIslands);

            ThreadUtil.sync(this::createInventory);
        } catch (Exception e) {
            this.errorHandling(e);
        }
    }

    public void createInventory() {
        try {
            List<IslandDto> islands = (List<IslandDto>) data.get("islands");

            int invSize = InventoryUtil.calculateInvSize(islands.size());

            Inventory inventory = Bukkit.createInventory(null, invSize, "Wybierz wyspę do odwiedzenia");

            for (IslandDto islandDto : islands) {
                inventory.addItem(InventoryUtil.createItemStack(islandDto));
            }

            Player executor = (Player) data.get("executor");

            executor.openInventory(inventory);

            this.closeDatabase();
        } catch (Exception e) {
            this.errorHandling(e);
        }

    }
}
