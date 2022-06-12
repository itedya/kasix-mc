package com.itedya.skymaster.runnables;

import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ShowIslandsForInvitesGuiRunnable extends BukkitRunnable {
    private Connection connection;
    private final Player player;
    private final List<ItemStack> itemStacks = new ArrayList<>();
    private List<IslandDto> userIslands;

    private final Player invitedPlayer;

    public ShowIslandsForInvitesGuiRunnable(Player player, Player invitedPlayer) {
        this.player = player;
        this.invitedPlayer = invitedPlayer;
    }

    @Override
    public void run() {
        try {
            this.connection = Database.getInstance().getConnection();

            IslandDao islandDao = new IslandDao(connection);

            this.userIslands = islandDao.getByOwnerUuidWithAllRelations(player.getUniqueId().toString());

            for (IslandDto island : userIslands) {
                ItemStack itemStack = new ItemStack(Material.GRASS_BLOCK);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("Wyspa " + island.getName());

                itemMeta.setLore(List.of(
                        ChatColor.YELLOW + "X: " + island.getHome().getX(),
                        ChatColor.YELLOW + "Z: " + island.getHome().getZ()
                ));

                itemStack.setItemMeta(itemMeta);
                itemStacks.add(itemStack);
            }

            ThreadUtil.sync(this::addOwnerNicknameToLore);

            this.connection.close();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Wystąpił błąd serwera.");
        }
    }

    // todo: change name of this function
    public void addOwnerNicknameToLore() {
        List<OfflinePlayer> owners = new ArrayList<>();

        this.userIslands.forEach(ele -> owners.add(Bukkit.getOfflinePlayer(ele.getOwnerUuid())));

        Inventory inventory = Bukkit.createInventory(null, 9, "Wybierz wyspę do której chcesz zaprosić");

        for (int i = 0; i < itemStacks.size(); i++) {
            IslandDto islandDto = userIslands.get(i);
            ItemStack itemStack = itemStacks.get(i);
            OfflinePlayer owner = owners.get(i);

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (i == 0) {
                PersistentDataContainerUtil.setString(
                        itemMeta.getPersistentDataContainer(),
                        "inventory-identifier",
                        "choose-island-invite-member-gui"
                );
            }

            PersistentDataContainerUtil.setInt(
                    itemMeta.getPersistentDataContainer(),
                    "island-id",
                    islandDto.getId()
            );

            PersistentDataContainerUtil.setString(
                    itemMeta.getPersistentDataContainer(),
                    "invite-to-player-uuid",
                    invitedPlayer.getUniqueId().toString()
            );

            List<String> lore = itemStack.getLore();
            lore.add("Właściciel: " + owner.getName());
            itemStack.setLore(lore);

            itemStack.setItemMeta(itemMeta);

            inventory.addItem(itemStack);
        }

        player.openInventory(inventory);
    }
}
