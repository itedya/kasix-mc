package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandHomeDao;
import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.dtos.IslandSchematicDto;
import com.itedya.skymaster.exceptions.ServerError;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListIslandsSubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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

            if (playerToCheck == null) {
                player.sendMessage(ChatColor.YELLOW + "Gracz z takim nickiem nie istnieje!");
                return true;
            }
        } else {
            // check if user has permission to list their islands
            if (!player.hasPermission("kasix-mc.islands.list")) {
                player.sendMessage(ChatColor.RED + "Brak permisji.");
                return true;
            }

            playerToCheck = player;
        }

        IslandDao islandDao = IslandDao.getInstance();
        IslandSchematicDao islandSchematicDao = IslandSchematicDao.getInstance();
        IslandHomeDao islandHomeDao = IslandHomeDao.getInstance();

        ThreadUtil.async(() -> this.stepOne(islandDao, islandSchematicDao, islandHomeDao, player, playerToCheck));

        return true;
    }

    public void stepOne(IslandDao islandDao, IslandSchematicDao islandSchematicDao, IslandHomeDao islandHomeDao,
                        Player player, Player playerToCheck) {
        try {
            String ownerUuid = playerToCheck.getUniqueId().toString();
            List<IslandDto> userIslands = islandDao.getByOwnerUuid(ownerUuid);
            List<IslandSchematicDto> schematics = islandSchematicDao.getAll();
            List<IslandHomeDto> homes = new ArrayList<>();

            for (IslandDto islandDto : userIslands) homes.add(islandHomeDao.firstByIslandId(islandDto.getId()));

            int size = 9;
            while (size < userIslands.size()) size += 9;

            int finalSize = size;
            ThreadUtil.sync(() -> this.stepTwo(player, playerToCheck, finalSize, userIslands, schematics, homes));
        } catch (ServerError e) {
            player.sendMessage(e.getMessage());
        }
    }

    public void stepTwo(Player player, Player playerToCheck, int size,
                        List<IslandDto> userIslands, List<IslandSchematicDto> schematics,
                        List<IslandHomeDto> islandHomes) {
        try {
            Inventory inventory = Bukkit.createInventory(null, size,
                    ChatColor.LIGHT_PURPLE + "Wyspy gracza " + playerToCheck.getName());

            for (int i = 0; i < userIslands.size(); i++) {
                IslandDto islandDto = userIslands.get(i);

                IslandSchematicDto schematic = schematics.stream().filter(ele -> ele.getId() == islandDto.getSchematicId())
                        .findFirst()
                        .orElse(null);

                if (schematic == null) {
                    throw new ServerError("Schematic is null ListIslandsSubCommand:99");
                }

                IslandHomeDto home = islandHomes.get(i);

                ItemStack itemStack = new ItemStack(schematic.getMaterial());
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("Wyspa " + islandDto.getName());

                itemMeta.setLore(List.of(
                        ChatColor.YELLOW + "X: " + home.getX(),
                        ChatColor.YELLOW + "Z: " + home.getZ()
                ));

                PersistentDataContainerUtil.add(itemMeta.getPersistentDataContainer(), "island-id", islandDto.getId());

                itemStack.setItemMeta(itemMeta);

                inventory.addItem(itemStack);
            }

            player.openInventory(inventory);
        } catch (ServerError e) {
            player.sendMessage(new ServerError().getMessage());
        }
    }
}
