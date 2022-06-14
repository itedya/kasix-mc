package com.itedya.skymaster.listeners;

import com.itedya.skymaster.runnables.island.RemoveIslandRunnable;
import com.itedya.skymaster.runnables.island.ResetWorldGuardPermissionsRunnable;
import com.itedya.skymaster.runnables.home.TeleportPlayerToIslandHomeRunnable;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IslandInfoGUIHandler implements Listener {
    public boolean react(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return false;

        ItemMeta itemMeta = firstItem.getItemMeta();
        String identifier = PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "inventory-identifier");
        if (identifier == null) return false;

        return identifier.equals("island-info-gui");
    }

    @EventHandler()
    public void onInvClick(InventoryClickEvent event) {
        if (!react(event)) return;

        event.setCancelled(true);

        try {
            if (!(event.getWhoClicked() instanceof Player player)) {
                throw new Exception("Entity that clicked is not a player.");
            }

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
                case REPEATING_COMMAND_BLOCK ->
                        ThreadUtil.async(new ResetWorldGuardPermissionsRunnable(player, islandId));
            }

            event.getWhoClicked().closeInventory();
        } catch (Exception e) {
            e.printStackTrace();
            event.getWhoClicked().sendMessage(ChatColor.RED + "Server error.");
            event.getInventory().close();
        }
    }


}