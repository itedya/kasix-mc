package com.itedya.skymaster.prompts.createislandschematic;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.conversations.Conversable;
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
        Conversable conversable = context.getForWhom();

        if (input == null) {
            conversable.sendRawMessage(new ComponentBuilder()
                    .append("Nie wpisałeś nazwy! Jeżeli chcesz wyjdź, wpisz ").color(ChatColor.YELLOW)
                    .append("wyjdz").color(ChatColor.RED)
                    .append(".").color(ChatColor.YELLOW)
                    .toString());
            return new ProvideIslandSchematicNamePrompt();
        }

        context.setSessionData("name", ChatColor.translateAlternateColorCodes('&', input));

        return new ProvideIslandSchematicDescriptionPrompt();
    }
}
