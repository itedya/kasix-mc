package com.itedya.skymaster.guihandlers;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.runnables.island.ShowIslandGuiRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// GUI LAUNCHER - ShowIslandListGuiRunnable
public class ListUserIslandsGUIHandler implements GUIHandler {
    public void onEvent(InventoryClickEvent event, Player player) {
        try {
            String userUuid = this.getUserUuid(event);
            assert userUuid != null : "User UUID is null!";

            ItemStack currentItem = event.getCurrentItem();

            if (currentItem == null) return;

            ItemMeta itemMeta = currentItem.getItemMeta();
            Integer islandId = PersistentDataContainerUtil.getInt(
                    itemMeta.getPersistentDataContainer(),
                    "island-id"
            );
            assert islandId != null : "Island ID is null!";

            new ShowIslandGuiRunnable(player, islandId)
                    .runTaskAsynchronously(SkyMaster.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatUtil.SERVER_ERROR);
        }
    }

    public String getUserUuid(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        assert firstItem != null : "First item is null!";

        ItemMeta itemMeta = firstItem.getItemMeta();
        return PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "user-uuid");
    }
}
