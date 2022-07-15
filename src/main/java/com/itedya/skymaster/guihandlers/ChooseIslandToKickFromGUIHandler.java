package com.itedya.skymaster.guihandlers;

import com.itedya.skymaster.runnables.kick.ShowMembersToKickRunnable;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ChooseIslandToKickFromGUIHandler implements GUIHandler {
    public void onEvent(InventoryClickEvent event, Player player) {
        var item = event.getCurrentItem();
        assert item != null;

        var meta = item.getItemMeta();

        var islandId = PersistentDataContainerUtil.getInt(meta.getPersistentDataContainer(), "island-id");
        assert islandId != null;

        ThreadUtil.async(new ShowMembersToKickRunnable(player, islandId));
    }
}
