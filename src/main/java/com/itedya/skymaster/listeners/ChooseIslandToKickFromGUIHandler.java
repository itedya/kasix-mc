package com.itedya.skymaster.listeners;

import com.itedya.skymaster.runnables.kick.ShowMembersToKickRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChooseIslandToKickFromGUIHandler implements Listener {
    public boolean react(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return false;

        ItemMeta itemMeta = firstItem.getItemMeta();
        String identifier = PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "inventory-identifier");
        if (identifier == null) return false;

        return identifier.equals("choose-island-to-kick-member-gui");
    }

    @EventHandler()
    public void onInvClick(InventoryClickEvent event) {
        if (!react(event)) return;

        event.setCancelled(true);

        try {
            if (!(event.getWhoClicked() instanceof Player player)) {
                return;
            }

            var item = event.getCurrentItem();
            assert item != null;

            var meta = item.getItemMeta();

            var islandId = PersistentDataContainerUtil.getInt(meta.getPersistentDataContainer(), "island-id");
            assert islandId != null;

            ThreadUtil.async(new ShowMembersToKickRunnable(islandId, player));
        } catch (Exception e) {
            e.printStackTrace();
            event.getWhoClicked().sendMessage(ChatUtil.getServerErrorMessage());
        }
    }
}
