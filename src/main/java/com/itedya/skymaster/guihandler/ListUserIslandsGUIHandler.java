package com.itedya.skymaster.guihandler;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.runnables.island.ShowIslandGuiRunnable;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// GUI LAUNCHER - ShowIslandListGuiRunnable
public class ListUserIslandsGUIHandler extends GUIHandler {
    public ListUserIslandsGUIHandler() {
        super("user-islands-gui");
    }

    @Override()
    public void onEvent(InventoryClickEvent event, Player player) throws Exception {
        String userUuid = this.getUserUuid(event);
        if (userUuid == null) {
            throw new Exception("User UUID is null");
        }

        ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null) return;

        ItemMeta itemMeta = currentItem.getItemMeta();
        Integer islandId = PersistentDataContainerUtil.getInt(
                itemMeta.getPersistentDataContainer(),
                "island-id"
        );
        if (islandId == null) {
            throw new Exception("Island ID is null");
        }

        new ShowIslandGuiRunnable(player, islandId)
                .runTaskAsynchronously(SkyMaster.getInstance());
    }

    public String getUserUuid(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);

        ItemMeta itemMeta = firstItem.getItemMeta();
        return PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "user-uuid");
    }
}
