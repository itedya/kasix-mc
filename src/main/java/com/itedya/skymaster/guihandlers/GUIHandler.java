package com.itedya.skymaster.guihandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface GUIHandler {
    default void onEvent(InventoryClickEvent event, Player player) {
    }
}
