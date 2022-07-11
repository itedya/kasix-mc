package com.itedya.skymaster.guihandler;

import com.itedya.skymaster.runnables.view.VisitIslandRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VisitIslandGUIHandler extends GUIHandler {
    public VisitIslandGUIHandler() {
        super("visit-island-gui");
    }

    @Override
    public void onEvent(InventoryClickEvent event, Player player) throws Exception {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;

        ItemMeta itemMeta = currentItem.getItemMeta();

        Integer islandId = PersistentDataContainerUtil.getInt(itemMeta.getPersistentDataContainer(), "island-id");
        assert islandId != null;

        ThreadUtil.async(new VisitIslandRunnable(player, islandId));
    }
}
