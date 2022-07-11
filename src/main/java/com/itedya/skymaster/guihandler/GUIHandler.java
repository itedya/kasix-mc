package com.itedya.skymaster.guihandler;

import com.itedya.skymaster.utils.PersistentDataContainerUtil;
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
}
