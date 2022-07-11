package com.itedya.skymaster.guihandler;

import com.itedya.skymaster.runnables.kick.ShowMembersToKickRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChooseIslandToKickFromGUIHandler extends GUIHandler {
    public ChooseIslandToKickFromGUIHandler() {
        super("choose-island-to-kick-member-gui");
    }

    @Override
    public void onEvent(InventoryClickEvent event, Player player) {
        try {
            var item = event.getCurrentItem();
            assert item != null;

            var meta = item.getItemMeta();

            var islandId = PersistentDataContainerUtil.getInt(meta.getPersistentDataContainer(), "island-id");
            assert islandId != null;

            ThreadUtil.async(new ShowMembersToKickRunnable(player, islandId));
        } catch (Exception e) {
            e.printStackTrace();
            event.getWhoClicked().sendMessage(ChatUtil.getServerErrorMessage());
        }
    }
}
