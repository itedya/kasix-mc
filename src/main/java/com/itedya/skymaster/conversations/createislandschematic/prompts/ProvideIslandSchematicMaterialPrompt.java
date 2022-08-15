package com.itedya.skymaster.conversations.createislandschematic.prompts;

import com.itedya.skymaster.utils.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProvideIslandSchematicMaterialPrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatUtil.PREFIX + " " +
                "%sPodaj materiał reprezentacyjny schematu. " .formatted(ChatColor.GRAY) +
                context.getSessionData("exitMessage");
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        Conversable conversable = context.getForWhom();

        if (input == null) {
            conversable.sendRawMessage(ChatColor.RED + "Nie podałeś materiału reprezentacyjnego.");
            return new ProvideIslandSchematicMaterialPrompt();
        }

        Material material;
        try {
            material = Material.valueOf(input);
        } catch (IllegalArgumentException e) {
            conversable.sendRawMessage(ChatColor.RED + "Materiał " + input + " nie istnieje!");
            return new ProvideIslandSchematicMaterialPrompt();
        }

        context.setSessionData("material", material);

        return new CheckWorldEditClipboardPrompt();
    }
}
