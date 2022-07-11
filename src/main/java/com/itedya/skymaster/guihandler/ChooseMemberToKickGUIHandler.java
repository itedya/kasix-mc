package com.itedya.skymaster.guihandler;

import com.itedya.skymaster.runnables.kick.KickPlayerFromIslandRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ChooseMemberToKickGUIHandler extends GUIHandler {
    public ChooseMemberToKickGUIHandler() {
        super("choose-member-to-kick-gui");
    }

    @Override
    public void onEvent(InventoryClickEvent event, Player player) {
        try {
            var item = event.getCurrentItem();
            assert item != null;

            var meta = item.getItemMeta();
            var container = meta.getPersistentDataContainer();

            var islandId = PersistentDataContainerUtil.getInt(container, "island-id");
            var memberUuid = PersistentDataContainerUtil.getString(container, "member-uuid");
            assert islandId != null;
            assert memberUuid != null;

            ThreadUtil.async(new KickPlayerFromIslandRunnable(player, islandId, memberUuid));
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatUtil.getServerErrorMessage());
        }
    }
}
