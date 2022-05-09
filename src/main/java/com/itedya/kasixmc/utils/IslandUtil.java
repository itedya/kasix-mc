package com.itedya.kasixmc.utils;

import com.itedya.kasixmc.KasixMC;
import com.itedya.kasixmc.dtos.IslandDto;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IslandUtil {
    public static List<ItemStack> convertIslandDtosToItemStacks(List<IslandDto> dtos) {
        List<ItemStack> itemStacks = new ArrayList<>();

        OfflinePlayer lastPlayer = null;

        for (int i = 0; i < dtos.size(); i++) {
            IslandDto ele = dtos.get(i);
            ItemStack itemStack = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("Wyspa " + i);

            if (lastPlayer == null || lastPlayer.getUniqueId().toString() != ele.getOwnerUUID()) {
                lastPlayer = Bukkit.getOfflinePlayer(UUID.fromString(ele.getOwnerUUID()));
            }

            itemMeta.setLore(List.of(
                    ChatColor.YELLOW + "X: " + ele.getHome().getBlockX(),
                    ChatColor.YELLOW + "Z: " + ele.getHome().getBlockZ(),
                    ChatColor.YELLOW + "Właściciel: " + lastPlayer.getName()
            ));
            itemStack.setItemMeta(itemMeta);

            itemMeta.getPersistentDataContainer().set(
                    new NamespacedKey(KasixMC.getInstance(), "island_uuid"),
                    PersistentDataType.STRING,
                    ele.getUuid()
            );

            itemStacks.add(itemStack);
        }

        return itemStacks;
    }
}
