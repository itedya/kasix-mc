package com.itedya.skymaster.listeners;

import com.itedya.skymaster.runnables.kick.KickPlayerFromIslandRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChooseMemberToKickGUIHandler implements Listener {
    public boolean react(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return false;

        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return false;

        ItemMeta itemMeta = firstItem.getItemMeta();
        String identifier = PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "inventory-identifier");
        if (identifier == null) return false;

        return identifier.equals("choose-member-to-kick-gui");
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
            var container = meta.getPersistentDataContainer();

            var islandId = PersistentDataContainerUtil.getInt(container, "island-id");
            var memberUuid = PersistentDataContainerUtil.getString(container, "member-uuid");
            assert islandId != null;
            assert memberUuid != null;

            ThreadUtil.async(new KickPlayerFromIslandRunnable(player, islandId, memberUuid));

            event.getInventory().close();
        } catch (Exception e) {
            e.printStackTrace();
            event.getWhoClicked().sendMessage(ChatUtil.getServerErrorMessage());
        }
    }
}
