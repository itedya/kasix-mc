package com.itedya.kasixmc.prompts.createislandschematic;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProvideIslandSchematicDescriptionPrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatColor.GRAY + "Podaj opis schematu";
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) {
            context.getForWhom()
                    .sendRawMessage(ChatColor.RED + "Nie podałeś opisu.");
            return new ProvideIslandSchematicDescriptionPrompt();
        }

        context.setSessionData("description", ChatColor.translateAlternateColorCodes('&', input));

        return new ProvideIslandSchematicFileNamePrompt();
    }
}
