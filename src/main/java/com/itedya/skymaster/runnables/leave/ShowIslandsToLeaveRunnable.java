package com.itedya.skymaster.runnables.leave;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.runnables.SkymasterRunnable;
import com.itedya.skymaster.utils.InventoryUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ShowIslandsToLeaveRunnable extends SkymasterRunnable {
    private final Player executor;
    private List<IslandDto> islands;
    private String memberUuid;

    public ShowIslandsToLeaveRunnable(Player executor) {
        super(executor, true);
        this.executor = executor;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            memberUuid = executor.getUniqueId().toString();
            IslandDao islandDao = new IslandDao(connection);
            islands = islandDao.getByMemberUuidWithAllRelations(memberUuid);

            ThreadUtil.sync(this::createInv);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }

    public void createInv() {
        try {
            Inventory inv = Bukkit.createInventory(null, InventoryUtil.calculateInvSize(islands.size()), "");

            for (IslandDto islandDto : islands) {
                ItemStack itemStack = InventoryUtil.createItemStack(islandDto);
                ItemMeta itemMeta = itemStack.getItemMeta();
                var container = itemMeta.getPersistentDataContainer();

                PersistentDataContainerUtil.setString(container, "inventory-identifier", "choose-island-to-leave-gui");
                PersistentDataContainerUtil.setInt(container, "island-id", islandDto.id);
                PersistentDataContainerUtil.setString(container, "member-uuid", memberUuid);

                itemStack.setItemMeta(itemMeta);
                inv.addItem(itemStack);
            }

            executor.openInventory(inv);
        } catch (Exception e) {
            super.errorHandling(e);
        }
    }
}
