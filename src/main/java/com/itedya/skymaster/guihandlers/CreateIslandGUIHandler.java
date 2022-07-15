package com.itedya.skymaster.guihandlers;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.conversations.createisland.ProvideIslandNamePrompt;
import com.itedya.skymaster.utils.*;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ExactMatchConversationCanceller;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// GUI LAUNCHER - ShowCreateIslandGuiRunnable
public class CreateIslandGUIHandler implements GUIHandler {
    public void onEvent(InventoryClickEvent event, Player player) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        ItemMeta itemMeta = item.getItemMeta();
        Integer schematicId = PersistentDataContainerUtil.getInt(itemMeta.getPersistentDataContainer(), "schematic-id");
        assert schematicId != null : "Schematic ID is null";

        new ConversationFactory(SkyMaster.getInstance())
                .withConversationCanceller(new ExactMatchConversationCanceller("wyjdz"))
                .withLocalEcho(false)
                .withFirstPrompt(new ProvideIslandNamePrompt(schematicId))
                .buildConversation(player)
                .begin();
    }
}
