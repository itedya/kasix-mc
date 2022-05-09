package com.itedya.skymaster.prompts.createislandschematic;

import com.itedya.skymaster.utils.IslandSchematicUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class ProvideIslandSchematicIdPrompt extends StringPrompt {

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        Conversable conversable = context.getForWhom();
        conversable.sendRawMessage("Aby wyjść, wpisz /wyjdz");
        return ChatColor.GRAY + "Podaj id schematu, np. non_sub, sub, wyspa_dla_graczy, wyspa_dla_vipow.";
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) {
            context.getForWhom().sendRawMessage(ChatColor.RED + "Nie podałeś ID");
            return new ProvideIslandSchematicIdPrompt();
        }

        try {
            if (IslandSchematicUtil.getById(input) != null) {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Schemat z takim ID już istnieje!");
                return new ProvideIslandSchematicIdPrompt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        context.setSessionData("id", input);

        return new ProvideIslandSchematicNamePrompt();
    }
}
