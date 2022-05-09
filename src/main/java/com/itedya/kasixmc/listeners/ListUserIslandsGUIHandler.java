package com.itedya.kasixmc.listeners;

import com.itedya.kasixmc.KasixMC;
import com.itedya.kasixmc.daos.IslandDao;
import com.itedya.kasixmc.dtos.IslandDto;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListUserIslandsGUIHandler implements Listener {
    private String guiTitle = ChatColor.LIGHT_PURPLE + "Wyspy gracza ";

    @EventHandler()
    public void onInvClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith(guiTitle)) return;

        event.setCancelled(true);

        try {
            if (!(event.getWhoClicked() instanceof Player player)) {
                throw new Exception("Entity that clicked is not a player.");
            }

            ItemStack currentItem = event.getCurrentItem();

            if (currentItem == null) return;
            if (currentItem.getType() != Material.GRASS_BLOCK) return;

            ItemMeta itemMeta = currentItem.getItemMeta();
            List<String> lore = itemMeta.getLore();
            String islandUuid = lore.get(lore.size() - 1);
            if (islandUuid == null) throw new Exception("Island UUID is null, something weird is happening.");

            IslandDao islandDao = IslandDao.getInstance();
            IslandDto islandDto = islandDao.getByUuid(islandUuid);

            player.closeInventory();

            showIslandGui(player, islandDto);
        } catch (Exception e) {
            e.printStackTrace();
            event.getWhoClicked().sendMessage(ChatColor.RED + "Server error.");
            event.getInventory().close();
        }
    }

    public void showIslandGui(Player whoClicked, IslandDto dto) {
        Inventory inventory = Bukkit.createInventory(null, 9, "Szczegóły wyspy");

        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(dto.getOwnerUUID()));

        inventory.addItem(getTeleportToIslandItem(dto));

        if ((player.getUniqueId().toString().equals(dto.getOwnerUUID()) &&
                whoClicked.hasPermission("kasix-mc.islands.remove")) ||
                ! player.getUniqueId().toString().equals(dto.getOwnerUUID()) &&
                        whoClicked.hasPermission("kasix-mc.islands.remove-someone")) {
            inventory.addItem(getRemoveItem(dto));
        }

        if ((player.getUniqueId().toString().equals(dto.getOwnerUUID()) &&
                whoClicked.hasPermission("kasix-mc.islands.reset-permissions")) ||
                ! player.getUniqueId().toString().equals(dto.getOwnerUUID()) &&
                        whoClicked.hasPermission("kasix-mc.islands.reset-permissions-someone")) {
            inventory.addItem(getResetWorldGuardItem(dto));
        }

        whoClicked.openInventory(inventory);
    }

    private ItemStack getTeleportToIslandItem(IslandDto dto) {
        ItemStack teleportToIslandItem = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta teleportToIslandItemMeta = teleportToIslandItem.getItemMeta();
        teleportToIslandItemMeta.setDisplayName(ChatColor.GREEN + "Teleportuj do domu wyspy");
        teleportToIslandItemMeta.setLore(List.of(
                ChatColor.YELLOW + "X: " + dto.getHome().getX(),
                ChatColor.YELLOW + "Z: " + dto.getHome().getZ()
        ));
        teleportToIslandItemMeta.getPersistentDataContainer().set(
                new NamespacedKey(KasixMC.getInstance(), "island_uuid"),
                PersistentDataType.STRING,
                dto.getUuid()
        );
        teleportToIslandItem.setItemMeta(teleportToIslandItemMeta);
        return teleportToIslandItem;
    }

    private ItemStack getRemoveItem(IslandDto dto) {
        ItemStack removeItem = new ItemStack(Material.BARRIER);
        ItemMeta removeItemMeta = removeItem.getItemMeta();
        removeItemMeta.setDisplayName(ChatColor.RED + "Usuń wyspę");
        removeItemMeta.getPersistentDataContainer().set(
                new NamespacedKey(KasixMC.getInstance(), "island_uuid"),
                PersistentDataType.STRING,
                dto.getUuid()
        );
        removeItem.setItemMeta(removeItemMeta);
        return removeItem;
    }

    private ItemStack getResetWorldGuardItem(IslandDto dto) {
        ItemStack resetWorldGuardItem = new ItemStack(Material.REPEATING_COMMAND_BLOCK);
        ItemMeta resetWorldGuardItemMeta = resetWorldGuardItem.getItemMeta();
        resetWorldGuardItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Zresetuj permisje WorldGuard");
        resetWorldGuardItemMeta.setLore(List.of(
                ChatColor.YELLOW + "Nie działa ci coś na wyspie? Nie możesz budować?",
                ChatColor.YELLOW + "Ktoś ma permisje do robienia czegoś mimo iż nie powinien?",
                ChatColor.GOLD + "Ten przycisk służy do naprawy takich rzeczy!"
        ));
        resetWorldGuardItemMeta.getPersistentDataContainer().set(
                new NamespacedKey(KasixMC.getInstance(), "island_uuid"),
                PersistentDataType.STRING,
                dto.getUuid()
        );
        resetWorldGuardItem.setItemMeta(resetWorldGuardItemMeta);
        return resetWorldGuardItem;
    }
}
