package com.itedya.skymaster.guihandlers;

import com.itedya.skymaster.runnables.island.RemoveIslandRunnable;
import com.itedya.skymaster.runnables.island.ResetWorldGuardPermissionsRunnable;
import com.itedya.skymaster.runnables.home.TeleportPlayerToIslandHomeRunnable;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IslandInfoGUIHandler extends GUIHandler {
    public IslandInfoGUIHandler() {
        super("island-info-gui");
    }

    @Override()
    public void onEvent(InventoryClickEvent event, Player player) {
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();

        Integer islandId = PersistentDataContainerUtil.getInt(
                itemMeta.getPersistentDataContainer(),
                "island-id"
        );

        Material material = itemStack.getType();

        switch (material) {
            case GRASS_BLOCK -> ThreadUtil.async(new TeleportPlayerToIslandHomeRunnable(player, islandId));
            case BARRIER -> ThreadUtil.async(new RemoveIslandRunnable(player, islandId));
            case REPEATING_COMMAND_BLOCK -> ThreadUtil.async(new ResetWorldGuardPermissionsRunnable(player, islandId));
        }
    }
}