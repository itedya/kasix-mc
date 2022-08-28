package com.itedya.skymaster.runnables.visit;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.VisitBlockDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.InventoryUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows ViewIsland GUI
 * <p>
 * Run asynchronously
 */
public class ShowVisitIslandGUIRunnable extends SkymasterRunnable {
    private final Player executor;
    private final OfflinePlayer owner;
    public ShowVisitIslandGUIRunnable(Player executor, OfflinePlayer owner) {
        super(executor, true);
        this.executor = executor;
        this.owner = owner;
    }

    private List<IslandDto> islands;

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);
            List<IslandDto> islandsToFilter = islandDao.getByOwnerUuidWithAllRelations(owner.getUniqueId().toString());
            islandsToFilter.addAll(islandDao.getByMemberUuidWithAllRelations(owner.getUniqueId().toString()));
            islands = new ArrayList<>();

            VisitBlockDao blockDao = new VisitBlockDao(connection);

            for (IslandDto island : islandsToFilter) {
                if (blockDao.get(island.ownerUuid, executor.getUniqueId().toString()) == null) {
                    islands.add(island);
                }
            }

            ThreadUtil.sync(this::createInventory);
        } catch (Exception e) {
            this.errorHandling(e);
        }
    }

    public void createInventory() {
        try {
            int invSize = InventoryUtil.calculateInvSize(islands.size());

            Inventory inventory = Bukkit.createInventory(null, invSize, "Wybierz wyspę do odwiedzenia");

            for (IslandDto islandDto : islands) {
                ItemStack itemStack = InventoryUtil.createItemStack(islandDto);
                ItemMeta itemMeta = itemStack.getItemMeta();
                PersistentDataContainerUtil.setString(itemMeta.getPersistentDataContainer(), "inventory-identifier", "visit-island-gui");
                itemStack.setItemMeta(itemMeta);
                inventory.addItem(itemStack);
            }

            executor.openInventory(inventory);

            this.closeDatabase();
        } catch (Exception e) {
            this.errorHandling(e);
        }

    }
}
