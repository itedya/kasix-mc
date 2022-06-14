package com.itedya.skymaster.listeners;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.runnables.ShowIslandGuiRunnable;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// GUI LAUNCHER - ShowIslandListGuiRunnable
public class ListUserIslandsGUIHandler implements Listener {
    @EventHandler()
    public void onInvClick(InventoryClickEvent event) {
        if (!this.react(event)) return;

        event.setCancelled(true);

        try {
            Player player = (Player) event.getWhoClicked();

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

            player.closeInventory();

            new ShowIslandGuiRunnable(player, islandId)
                    .runTaskAsynchronously(SkyMaster.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
            event.getWhoClicked().sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            event.getInventory().close();
        }
    }

    public boolean react(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return false;

        ItemMeta itemMeta = firstItem.getItemMeta();
        String identifier = PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "inventory-identifier");
        if (identifier == null) return false;

        return identifier.equals("user-islands-gui");
    }

    public String getUserUuid(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);

        ItemMeta itemMeta = firstItem.getItemMeta();
        return PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "user-uuid");
    }
}
