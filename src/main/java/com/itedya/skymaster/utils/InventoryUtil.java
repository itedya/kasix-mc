package com.itedya.skymaster.utils;

import com.itedya.skymaster.dtos.IslandDto;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InventoryUtil {
    /**
     * Creates item stack for GUI IslandDTO representation
     * <p>
     * SYNCHRONOUS FUNC
     *
     * @param islandDto IslandDTO
     * @return ItemStack
     */
    public static ItemStack createItemStack(IslandDto islandDto) {
        var schematic = islandDto.getSchematic();
        var home = islandDto.getHome();

        var itemStack = new ItemStack(schematic.getMaterial());
        var meta = itemStack.getItemMeta();

        // set item name
        meta.setDisplayNameComponent(new ComponentBuilder()
                .color(ChatColor.YELLOW)
                .append("Wyspa ")
                .append("\"" + islandDto.getName() + "\"").bold(true)
                .create());

        var lore = meta.getLoreComponents();

        var player = Bukkit.getOfflinePlayer(UUID.fromString(islandDto.getOwnerUuid()));

        // set item lore - island owner
        lore.add(new ComponentBuilder()
                .color(ChatColor.GRAY)
                .append("Właściciel: ")
                .append(player.getName()).bold(true)
                .create());

        lore.add(new ComponentBuilder()
                .color(ChatColor.GRAY)
                .append("X Domu: ")
                .append("" + home.getX()).bold(true)
                .create());

        lore.add(new ComponentBuilder()
                .color(ChatColor.GRAY)
                .append("Z Domu: ")
                .append("" + home.getZ()).bold(true)
                .create());

        lore.add(new ComponentBuilder()
                .color(ChatColor.GRAY)
                .append("Nazwa schematu: ")
                .append(schematic.getName()).bold(true)
                .create());

        meta.setLoreComponents(lore);

        var dataContainer = meta.getPersistentDataContainer();

        PersistentDataContainerUtil.setInt(dataContainer, "island-id", islandDto.getId());

        return itemStack;
    }
}
