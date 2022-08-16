package com.itedya.skymaster.guihandlers;

import com.itedya.skymaster.runnables.invite.AddPlayerToIslandRunnable;
import com.itedya.skymaster.runnables.invite.InvitePlayerToIslandRunnable;
import com.itedya.skymaster.runnables.leave.LeaveIslandRunnable;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class ChooseIslandToLeaveGUIHandler implements GUIHandler {
    public void onEvent(InventoryClickEvent event, Player player) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null) return;

        int islandId = getIslandId(currentItem);

        ThreadUtil.sync(new LeaveIslandRunnable(player, islandId));
    }

    public int getIslandId(ItemStack itemStack) {
        var meta = itemStack.getItemMeta();
        var container = meta.getPersistentDataContainer();

        return PersistentDataContainerUtil.getInt(container, "island-id");
    }
}
