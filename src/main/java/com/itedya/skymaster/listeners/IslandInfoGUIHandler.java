package com.itedya.skymaster.listeners;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.utils.IslandHomeUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class IslandInfoGUIHandler implements Listener {
    private String guiTitle = "Szczegóły wyspy";

    @EventHandler()
    public void onInvClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith(guiTitle)) return;

        event.setCancelled(true);

        try {
            if (!(event.getWhoClicked() instanceof Player player)) {
                throw new Exception("Entity that clicked is not a player.");
            }

            ItemStack itemStack = event.getCurrentItem();
            Material material = itemStack.getType();
            String islandUUID = itemStack.getItemMeta().getPersistentDataContainer().get(
                    new NamespacedKey(SkyMaster.getInstance(), "island_uuid"),
                    PersistentDataType.STRING
            );

            IslandDao islandDao = IslandDao.getInstance();
            IslandDto islandDto = islandDao.getByUuid(islandUUID);

            switch (material) {
                case GRASS_BLOCK -> IslandHomeUtil.addPlayerToQueue(player, islandDto);
                case BARRIER -> {
                    WorldGuardUtil.removeRegionForDto(islandDto);
                    islandDto.setDeleted(true);
                    islandDao.update(islandDto);
                    player.sendMessage(ChatColor.GREEN + "Usunięto wyspę.");
                }
                case REPEATING_COMMAND_BLOCK -> {
                    resetWorldGuardPermissions(islandDto);
                    player.sendMessage(ChatColor.GREEN + "Zresetowano ustawienia WorldGuard dla tej wyspy.");
                }
            }

            event.getWhoClicked().closeInventory();
        } catch (Exception e) {
            e.printStackTrace();
            event.getWhoClicked().sendMessage(ChatColor.RED + "Server error.");
            event.getInventory().close();
        }
    }

    public void resetWorldGuardPermissions(IslandDto islandDto) {
        ProtectedRegion protectedRegion = WorldGuardUtil.getRegionForDto(islandDto);
        WorldGuardUtil.resetRegionFlags(protectedRegion);
        WorldGuardUtil.resetRegionMembers(protectedRegion, islandDto);
        WorldGuardUtil.resetPriority(protectedRegion);
    }
}