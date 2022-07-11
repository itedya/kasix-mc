package com.itedya.skymaster.guihandlers;

import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class GUIHandler implements Listener {
    protected String inventoryIdentifier;

    public GUIHandler(String identifier) {
        this.inventoryIdentifier = identifier;
    }

    protected boolean react(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return false;

        ItemMeta itemMeta = firstItem.getItemMeta();
        String identifier = PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "inventory-identifier");
        if (identifier == null) return false;

        return identifier.equals(this.inventoryIdentifier);
    }

    @EventHandler()
    private void onInvClick(InventoryClickEvent event) {
        if (!react(event)) return;
        event.setCancelled(true);
        if (!(event instanceof Player player)) return;

        try {
            this.onEvent(event, player);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatUtil.getServerErrorMessage());
        }

        event.getInventory().close();
    }

    public void onEvent(InventoryClickEvent event, Player player) throws Exception {
        // override this function
    }
}
