package com.itedya.skymaster.guihandlers;

import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.PersistentDataContainerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class GUIListener implements Listener {
    public GUIListener() {
    }

    public Map<String, GUIHandler> handlers = Map.of(
            "choose-island-invite-member-gui", new ChooseIslandInviteMemberGUIHandler(),
            "choose-island-to-kick-member-gui", new ChooseIslandToKickFromGUIHandler(),
            "choose-member-to-kick-gui", new ChooseMemberToKickGUIHandler(),
            "create-island-choose-schematic-gui", new CreateIslandGUIHandler(),
            "island-info-gui", new IslandInfoGUIHandler(),
            "user-islands-gui", new ListUserIslandsGUIHandler(),
            "visit-island-gui", new VisitIslandGUIHandler()
    );

    protected String getHandlerIdentifier(InventoryClickEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return null;

        ItemMeta itemMeta = firstItem.getItemMeta();
        return PersistentDataContainerUtil.getString(itemMeta.getPersistentDataContainer(), "inventory-identifier");
    }

    @EventHandler()
    private void onInvClick(InventoryClickEvent event) {
        var handlerIdentifier = getHandlerIdentifier(event);

        if (handlerIdentifier == null) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        try {
            var handler = this.handlers.get(handlerIdentifier);
            if (handler == null) return;

            handler.onEvent(event, player);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatUtil.SERVER_ERROR);
        }

        event.getInventory().close();
    }
}
