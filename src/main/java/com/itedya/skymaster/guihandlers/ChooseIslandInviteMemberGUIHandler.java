package com.itedya.skymaster.guihandlers;

import com.itedya.skymaster.runnables.invite.AddPlayerToIslandRunnable;
import com.itedya.skymaster.runnables.invite.InvitePlayerToIslandRunnable;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
public class ChooseIslandInviteMemberGUIHandler implements GUIHandler {
    public void onEvent(InventoryClickEvent event, Player player) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;

        ItemMeta itemMeta = currentItem.getItemMeta();

        Integer islandId = PersistentDataContainerUtil.getInt(itemMeta.getPersistentDataContainer(), "island-id");
        assert islandId != null;

        var playerToInviteUuid = getPlayerToInviteUuid(currentItem);
        var islandOwnerUuid = getIslandOwnerPlayerUuid(currentItem);
        var withAccept = getWithAccept(currentItem);

        if (withAccept) {
            var playerToInvite = Bukkit.getPlayer(UUID.fromString(playerToInviteUuid));
            var islandOwner = Bukkit.getPlayer(UUID.fromString(islandOwnerUuid));

            if (playerToInvite == null) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Gracz nie jest online!");
                return;
            }

            ThreadUtil.async(new InvitePlayerToIslandRunnable(player, islandOwner, playerToInvite, islandId));
        } else {
            var playerToInvite = Bukkit.getOfflinePlayer(UUID.fromString(playerToInviteUuid));

            ThreadUtil.sync(new AddPlayerToIslandRunnable(player, playerToInvite, islandId));
        }
    }

    public String getIslandOwnerPlayerUuid(ItemStack itemStack) {
        var meta = itemStack.getItemMeta();
        var container = meta.getPersistentDataContainer();

        return PersistentDataContainerUtil.getString(container, "island-owner-uuid");
    }

    public String getPlayerToInviteUuid(ItemStack itemStack) {
        var meta = itemStack.getItemMeta();
        var container = meta.getPersistentDataContainer();

        return PersistentDataContainerUtil.getString(container, "invite-to-player-uuid");
    }

    public Boolean getWithAccept(ItemStack itemStack) {
        var meta = itemStack.getItemMeta();
        var container = meta.getPersistentDataContainer();

        Integer value = PersistentDataContainerUtil.getInt(container, "with-accept");

        return value == 1;
    }
}
