package com.itedya.skymaster.conversations.createislandschematic.prompts;

import com.itedya.skymaster.conversations.ConversationPrompt;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ProvideIslandSchematicDescriptionPrompt implements ConversationPrompt {
    @Override
    public String getMessage(Player player) {
        return ChatColor.GRAY + "Podaj opis schematu";
    }

    @Override
    public ConversationPrompt parse(Map session, Player player, String message) {
        if (message == null) {
            player.sendMessage(ChatColor.RED + "Nie podałeś opisu.");
            return new ProvideIslandSchematicDescriptionPrompt();
        }

        session.put("description", ChatColor.translateAlternateColorCodes('&', message));

        return new ProvideIslandSchematicFileNamePrompt();
    }
}
