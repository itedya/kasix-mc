package com.itedya.kasixmc.command.subcommands;

import com.itedya.kasixmc.daos.IslandDao;
import com.itedya.kasixmc.dtos.IslandDto;
import com.itedya.kasixmc.dtos.IslandSchematicDto;
import com.itedya.kasixmc.utils.ConfigUtil;
import com.itedya.kasixmc.utils.IslandSchematicUtil;
import com.itedya.kasixmc.utils.PlayerUtil;
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

import java.util.List;


public class CreateIslandSubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            // check if user is in game
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ConfigUtil.getColouredString("messages.youHaveToBeInGame",
                        "Musisz być w grze, aby użyć tej komendy!"));
                return true;
            }

            // check if user has permission
            if (!player.hasPermission("kasix-mc.islands.create")) {
                sender.sendMessage(ConfigUtil.getColouredString("messages.youDontHavePermission",
                        "&4Brak permisji!"));
                return true;
            }

            // check if user exceeded max number of islands
            IslandDao islandDao = IslandDao.getInstance();
            Integer userIslands = islandDao.getByOwnerUuid(player.getUniqueId().toString()).size();
            Integer maxAllowedIslands = PlayerUtil.getMaxAllowedIslands(player);
            if (userIslands >= maxAllowedIslands) {
                player.sendMessage(ChatColor.YELLOW + "Nie możesz stworzyć tyle wysp! Max: " + maxAllowedIslands);
                return true;
            }

            // get allowed island schematics
            List<IslandSchematicDto> islandSchematics = IslandSchematicUtil.getWithPermission(player);

            // if island schematics is empty, show error
            if (islandSchematics.size() == 0) {
                player.sendMessage(ConfigUtil.getColouredString("message.youDontHaveAccessToAnySchematic",
                        "&eNie masz dostępu do żadnego schematu, więc nie możesz stworzyć wyspy!"));
                return true;
            }

            // count the size of gui
            int size = 9;
            while (size < islandSchematics.size()) size += 9;

            // create gui
            Inventory gui = Bukkit.createInventory(null, 9, ConfigUtil.getColouredString("message.islandSchematics",
                    "&dSchematy wysp"));

            islandSchematics.forEach(ele -> {
                ItemStack itemStack = new ItemStack(ele.getMaterial());
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ele.getName());
                itemStack.setItemMeta(itemMeta);

                gui.addItem(itemStack);
            });

            player.openInventory(gui);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Server Error.");
            return true;
        }
    }
}
