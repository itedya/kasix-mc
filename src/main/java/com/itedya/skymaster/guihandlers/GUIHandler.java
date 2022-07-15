package com.itedya.skymaster.guihandlers;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface GUIHandler {
    default void onEvent(InventoryClickEvent event) {
    }
}
