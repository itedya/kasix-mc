package com.itedya.skymaster.utils;

import com.itedya.skymaster.dtos.database.IslandDto;
import com.itedya.skymaster.dtos.database.IslandMemberDto;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class InventoryUtil {
    /**
     * Calculates GUI inventory size
     *
     * @param items size of array of items
     * @return integer
     */
    public static int calculateInvSize(int items) {
        int result = ((int) (Math.ceil(items / 9.0) + 0.5)) * 9;

        return result == 0 ? 9 : result;
    }

    /**
     * Creates item stack for GUI IslandDTO representation
     * <p>
     * SYNCHRONOUS FUNC
     *
     * @param islandDto IslandDTO
     * @return ItemStack
     */
    public static ItemStack createItemStack(IslandDto islandDto) {
        var schematic = islandDto.schematic;
        var home = islandDto.home;

        var itemStack = new ItemStack(schematic.material);
        var meta = itemStack.getItemMeta();

        // set item name
        meta.setDisplayName(ChatColor.YELLOW + "" + "Wyspa " + ChatColor.BOLD + "\"" + islandDto.name + "\"");

        var lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();

        var player = Bukkit.getOfflinePlayer(UUID.fromString(islandDto.ownerUuid));

        lore.add(ChatColor.GRAY + "Właściciel: " + ChatColor.BOLD + player.getName());
        lore.add(ChatColor.GRAY + "X Domu: " + ChatColor.BOLD + home.x);
        lore.add(ChatColor.GRAY + "Z Domu: " + ChatColor.BOLD + home.z);
        lore.add(ChatColor.GRAY + "Wielkość wyspy: " + ChatColor.BOLD + (islandDto.radius * 2));
        lore.add(ChatColor.GRAY + "Nazwa schematu: " + ChatColor.BOLD + schematic.name);

        meta.setLore(lore);

        var dataContainer = meta.getPersistentDataContainer();

        PersistentDataContainerUtil.setInt(dataContainer, "island-id", islandDto.id);

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    /**
     * Creates item stack for GUI IslandMemberDTO representation
     * <p>
     * SYNCHRONOUS FUNC
     *
     * @param memberDto IslandDTO
     * @return ItemStack
     */
    public static ItemStack createItemStack(IslandMemberDto memberDto) {
        var player = Bukkit.getOfflinePlayer(UUID.fromString(memberDto.playerUuid));

        var itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + player.getName());

        // set item name
        var lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "Dodany: " + ChatColor.BOLD + memberDto.createdAt);

        meta.setLore(lore);

        var dataContainer = meta.getPersistentDataContainer();

        PersistentDataContainerUtil.setString(dataContainer, "member-id", player.getUniqueId().toString());

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
