package com.itedya.skymaster.listeners;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.exceptions.ServerError;
import com.itedya.skymaster.runnables.InvitePlayerToIslandRunnable;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class InviteMemberGUIHandler implements Listener {
    public boolean react(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return false;

        ItemMeta itemMeta = firstItem.getItemMeta();
        String identifier = PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "inventory-identifier");
        if (identifier == null) return false;

        return identifier.equals("choose-island-invite-member-gui");
    }

    @EventHandler()
    public void onInvClick(InventoryClickEvent event) {
        if (!react(event)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        try {
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null) return;

            ItemMeta itemMeta = currentItem.getItemMeta();

            Integer islandId = PersistentDataContainerUtil.getInt(
                    itemMeta.getPersistentDataContainer(),
                    "island-id"
            );

            if (islandId == null) {
                throw new ServerError("Island id is null!");
            }

            String inviteToPlayerUuid = itemMeta.getPersistentDataContainer().get(
                    new NamespacedKey(SkyMaster.getInstance(), "invite-to-player-uuid"),
                    PersistentDataType.STRING
            );
            if (inviteToPlayerUuid == null) {
                throw new Exception("Invite to player UUID is null");
            }

            Player inviteToPlayer = Bukkit.getPlayer(UUID.fromString(inviteToPlayerUuid));
            if (inviteToPlayer == null) {
                player.sendMessage(ChatColor.YELLOW + "Gracza aktualnie nie ma na serwerze.");
                return;
            }

            ThreadUtil.async(new InvitePlayerToIslandRunnable(islandId, player, inviteToPlayer));
        } catch (Exception e) {
            e.printStackTrace();
            event.getInventory().close();
            event.getWhoClicked().sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
        }
    }
}
