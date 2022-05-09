package com.itedya.skymaster.listeners;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.IslandDto;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class InviteMemberGUIHandler {
    private final String guiTitle = "Wybierz wyspę do której chcesz zaprosić";

    @EventHandler()
    public void onInvClick(InventoryClickEvent event) {
        if (!guiTitle.equals(event.getView().getTitle())) return;

        event.setCancelled(true);

        try {
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem.getType() != Material.GRASS_BLOCK) return;

            ItemMeta itemMeta = currentItem.getItemMeta();

            String islandUuid = itemMeta.getPersistentDataContainer().get(
                    new NamespacedKey(SkyMaster.getInstance(), "island_uuid"),
                    PersistentDataType.STRING
            );

            IslandDao islandDao = IslandDao.getInstance();
            IslandDto islandDto = islandDao.getByUuid(islandUuid);

            String inviteFromPlayerUuid = itemMeta.getPersistentDataContainer().get(
                    new NamespacedKey(SkyMaster.getInstance(), "invite_from_player_uuid"),
                    PersistentDataType.STRING
            );

            Player inviteFromPlayer = Bukkit.getPlayer(UUID.fromString(inviteFromPlayerUuid));
            if (inviteFromPlayer == null) throw new Exception("Invite from player is null InviteMemberGUIHandler:49");

            String invitedPlayerUuid = itemMeta.getPersistentDataContainer().get(
                    new NamespacedKey(SkyMaster.getInstance(), "invite_to_player_uuid"),
                    PersistentDataType.STRING
            );

            Player inviteToPlayer = Bukkit.getPlayer(UUID.fromString(invitedPlayerUuid));
        } catch (Exception e) {
            e.printStackTrace();
            event.getInventory().close();
            event.getWhoClicked().sendMessage(ChatColor.RED + "Server error.");
        }
    }
}
