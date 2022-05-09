package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListIslandsSubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            // check if user is in game
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Musisz być w grze, aby wykonać tą komendę.");
                return true;
            }

            Player playerToCheck;

            // check if user is checking someone's islands
            if (args.length > 0) {
                // check if user has permission to list someone's islands
                if (!player.hasPermission("kasix-mc.islands.list-someone")) {
                    player.sendMessage(ChatColor.RED + "Brak permisji.");
                    return true;
                }

                playerToCheck = Bukkit.getPlayer(args[0]);
            } else {
                // check if user has permission to list their islands
                if (!player.hasPermission("kasix-mc.islands.list")) {
                    player.sendMessage(ChatColor.RED + "Brak permisji.");
                    return true;
                }

                playerToCheck = player;
            }

            IslandDao islandDao = IslandDao.getInstance();
            List<IslandDto> userIslands = islandDao.getByOwnerUuid(playerToCheck.getUniqueId().toString());

            int size = 9;
            while (size < userIslands.size()) size += 9;

            Inventory inventory = Bukkit.createInventory(null, size, ChatColor.LIGHT_PURPLE + "Wyspy gracza " + playerToCheck.getName());

            for (int i = 0; i < userIslands.size(); i++) {
                IslandDto islandDto = userIslands.get(i);
                ItemStack itemStack = new ItemStack(Material.GRASS_BLOCK);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("Wyspa " + (i + 1));

                itemMeta.setLore(List.of(
                        ChatColor.YELLOW + "X: " + islandDto.getHome().getX(),
                        ChatColor.YELLOW + "Z: " + islandDto.getHome().getZ(),
                        islandDto.getUuid()
                ));
                itemStack.setItemMeta(itemMeta);

                inventory.addItem(itemStack);
            }

            player.openInventory(inventory);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Server error.");
        }

        return true;
    }
}
