package com.itedya.skymaster.conversations.createislandschematic.prompts;

import com.itedya.skymaster.utils.ChatUtil;
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
        return new StringBuilder()
                .append(ChatUtil.PREFIX + " ")
                .append("%sPodaj nazwę schematu. " .formatted(ChatColor.GRAY))
                .append(context.getSessionData("exitMessage"))
                .toString();
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        Conversable conversable = context.getForWhom();

        if (input == null) {
            conversable.sendRawMessage(new ComponentBuilder()
                    .append("Nie wpisałeś nazwy!").color(ChatColor.YELLOW)
                    .toString());
            return new ProvideIslandSchematicNamePrompt();
        }

        context.setSessionData("name", ChatColor.translateAlternateColorCodes('&', input));

        return new ProvideIslandSchematicDescriptionPrompt();
    }
}
