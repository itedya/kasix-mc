package com.itedya.kasixmc.command.subcommands;

import com.itedya.kasixmc.KasixMC;
import com.itedya.kasixmc.daos.IslandDao;
import com.itedya.kasixmc.daos.IslandInviteDao;
import com.itedya.kasixmc.dtos.IslandDto;
import com.itedya.kasixmc.utils.IslandUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddIslandMemberSubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Musisz być w grze, aby wykonać tą komendę!");
                return true;
            }

            if (!player.hasPermission("kasix-mc.islands.invite")) {
                player.sendMessage(ChatColor.RED + "Brak permisji.");
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(ChatColor.YELLOW + "Nie wprowadziłeś nicku gracza, którego chcesz zaprosić.");
                return true;
            }

            if (args[0].equals(player.getName())) {
                player.sendMessage(ChatColor.YELLOW + "Nie możesz zaprosić sam siebie!");
            }

            Player invitedPlayer = Bukkit.getPlayer(args[0]);
            if (invitedPlayer == null) {
                player.sendMessage(ChatColor.YELLOW + "Gracz z takim nickiem nie istnieje albo nie jest online.");
                return true;
            }

            IslandInviteDao islandInviteDao = IslandInviteDao.getInstance();
            if (islandInviteDao.doesPlayerHaveInvite(invitedPlayer.getUniqueId().toString())) {
                player.sendMessage(ChatColor.YELLOW + "Gracz " + invitedPlayer.getName() + " ma już zaproszenie na wyspę, poczekaj do 30 sekund aż wygaśnie.");
                return true;
            }

            Inventory inventory = Bukkit.createInventory(null, 9, "Wybierz wyspę do której chcesz zaprosić");

            IslandDao islandDao = IslandDao.getInstance();
            List<IslandDto> userIslands = islandDao.getByOwnerUuid(player.getUniqueId().toString());

            IslandUtil.convertIslandDtosToItemStacks(userIslands)
                    .stream()
                    .forEach(ele -> {
                        ItemMeta itemMeta = ele.getItemMeta();
                        itemMeta.getPersistentDataContainer().set(
                                new NamespacedKey(KasixMC.getInstance(), "invite_from_player_uuid"),
                                PersistentDataType.STRING,
                                player.getUniqueId().toString()
                        );

                        itemMeta.getPersistentDataContainer().set(
                                new NamespacedKey(KasixMC.getInstance(), "invite_to_player_uuid"),
                                PersistentDataType.STRING,
                                invitedPlayer.getUniqueId().toString()
                        );
                        ele.setItemMeta(itemMeta);

                        inventory.addItem(ele);
                    });

            player.openInventory(inventory);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Server error.");
            return true;
        }
    }
}
