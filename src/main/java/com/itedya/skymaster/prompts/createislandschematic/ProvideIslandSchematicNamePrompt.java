package com.itedya.skymaster.prompts.createislandschematic;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProvideIslandSchematicNamePrompt extends StringPrompt {

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatColor.GRAY + "Podaj nazwę schematu.";
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) {
            context.getForWhom()
                    .sendRawMessage(ChatColor.RED + "Nie wpisałeś nazwy! Jeżeli chcesz wyjdź, wpisz /wyjdz.");
            return new ProvideIslandSchematicNamePrompt();
        }

        context.setSessionData("name", ChatColor.translateAlternateColorCodes('&', input));

        return new ProvideIslandSchematicDescriptionPrompt();
    }
}
