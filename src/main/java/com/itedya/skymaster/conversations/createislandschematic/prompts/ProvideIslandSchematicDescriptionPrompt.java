package com.itedya.skymaster.conversations.createislandschematic.prompts;

import com.itedya.skymaster.utils.ChatUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ProvideIslandSchematicDescriptionPrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return new StringBuilder()
                .append(ChatUtil.PREFIX + " ")
                .append("%sPodaj opis schematu. " .formatted(ChatColor.GRAY))
                .append(context.getSessionData("exitMessage"))
                .toString();
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        Conversable player = context.getForWhom();

        if (input == null) {
            player.sendRawMessage("%sNie podałeś opisu." .formatted(ChatColor.RED));
            return new ProvideIslandSchematicDescriptionPrompt();
        }

        context.setSessionData("description", ChatUtil.p(input));

        return new ProvideIslandSchematicMaterialPrompt();
    }
}
