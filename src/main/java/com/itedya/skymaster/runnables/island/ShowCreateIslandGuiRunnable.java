package com.itedya.skymaster.runnables.island;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.dtos.database.IslandSchematicDto;
import com.itedya.skymaster.utils.IslandUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.PlayerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

// GUI HANDLER - CreateIslandGUIHandler
public class ShowCreateIslandGuiRunnable extends BukkitRunnable {
    private Connection connection;
    private final Player player;
    private int userIslands;
    private List<IslandSchematicDto> schematics;

    public ShowCreateIslandGuiRunnable(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();
            this.userIslands = IslandUtil.getIslandAmount(connection, player.getUniqueId().toString());
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
            return;
        }

        ThreadUtil.sync(this::fetchMaxAllowedIslands);
    }

    public void fetchMaxAllowedIslands() {
        int maxAllowedIslands;
        try {
            maxAllowedIslands = PlayerUtil.getMaxAllowedIslands(player);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
            return;
        }

        if (userIslands >= maxAllowedIslands) {
            player.sendMessage(ChatColor.YELLOW + "Nie możesz stworzyć tyle wysp! Max: " + maxAllowedIslands);
            this.shutdown();
            return;
        }

        ThreadUtil.sync(this::getSchematics);
    }

    public void getSchematics() {
        // get allowed island schematics
        try {
            IslandSchematicDao dao = new IslandSchematicDao(this.connection);

            this.schematics = new ArrayList<>();

            for (IslandSchematicDto schematic : dao.getAll()) {
                boolean hasPermission = player.hasPermission("skymaster.islands.use-schematic." + schematic.id);

                if (hasPermission) {
                    this.schematics.add(schematic);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
            this.shutdown();
            return;
        }

        // if island schematics is empty, show error
        if (this.schematics.size() == 0) {
            player.sendMessage(ChatColor.YELLOW + "Nie masz dostępu do żadnego schematu, więc nie możesz stworzyć wyspy!");
            return;
        }

        ThreadUtil.sync(this::showGui);
    }

    public void showGui() {
        // count the size of gui
        int size = 9;
        while (size < schematics.size()) size += 9;

        // create gui
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.LIGHT_PURPLE + "Schematy wysp");

        for (int i = 0; i < schematics.size(); i++) {
            IslandSchematicDto schematic = schematics.get(i);

            ItemStack itemStack = new ItemStack(schematic.material);
            ItemMeta itemMeta = itemStack.getItemMeta();

            PersistentDataContainerUtil.setString(itemMeta.getPersistentDataContainer(), "inventory-identifier", "create-island-choose-schematic-gui");
            PersistentDataContainerUtil.setInt(itemMeta.getPersistentDataContainer(), "schematic-id", schematic.id);

            itemMeta.setLore(List.of(schematic.description));
            itemMeta.setDisplayName(schematic.name);
            itemStack.setItemMeta(itemMeta);

            gui.addItem(itemStack);
        }

        player.openInventory(gui);
    }

    public void shutdown() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
