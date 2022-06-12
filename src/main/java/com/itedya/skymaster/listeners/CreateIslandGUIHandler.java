package com.itedya.skymaster.listeners;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.conversations.createisland.ProvideIslandNamePrompt;
import com.itedya.skymaster.exceptions.ServerError;
import com.itedya.skymaster.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ExactMatchConversationCanceller;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// GUI LAUNCHER - ShowCreateIslandGuiRunnable
public class CreateIslandGUIHandler implements Listener {
    public boolean react(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return false;

        ItemMeta itemMeta = firstItem.getItemMeta();
        String identifier = PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "inventory-identifier");
        if (identifier == null) return false;

        return identifier.equals("create-island-choose-schematic-gui");
    }

    @EventHandler()
    public void onInvClick(InventoryClickEvent event) {
        if (!react(event)) return;

        event.setCancelled(true);

        try {
            if (!(event.getWhoClicked() instanceof Player player)) {
                return;
            }

            ItemStack item = event.getCurrentItem();
            if (item == null) return;

            ItemMeta itemMeta = item.getItemMeta();
            Integer schematicId = PersistentDataContainerUtil.getInt(itemMeta.getPersistentDataContainer(), "schematic-id");
            if (schematicId == null) {
                throw new ServerError("Schematic ID is null");
            }

            new ConversationFactory(SkyMaster.getInstance())
                    .withConversationCanceller(new ExactMatchConversationCanceller("wyjdz"))
                    .withLocalEcho(false)
                    .withFirstPrompt(new ProvideIslandNamePrompt(schematicId))
                    .buildConversation(player)
                    .begin();
        } catch (Exception e) {
            e.printStackTrace();
            event.getWhoClicked().sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
        }

        event.getInventory().close();
    }
}
