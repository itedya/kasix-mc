package com.itedya.skymaster.guihandlers;

import com.itedya.skymaster.runnables.kick.KickPlayerFromIslandRunnable;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ChooseMemberToKickGUIHandler implements GUIHandler {
    public void onEvent(InventoryClickEvent event, Player player) {
        var item = event.getCurrentItem();
        assert item != null;

        var meta = item.getItemMeta();
        var container = meta.getPersistentDataContainer();

        var islandId = PersistentDataContainerUtil.getInt(container, "island-id");
        var memberUuid = PersistentDataContainerUtil.getString(container, "member-uuid");
        assert islandId != null;
        assert memberUuid != null;

        ThreadUtil.async(new KickPlayerFromIslandRunnable(player, islandId, memberUuid));
    }
}
