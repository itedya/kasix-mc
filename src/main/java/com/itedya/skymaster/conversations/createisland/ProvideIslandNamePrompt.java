package com.itedya.skymaster.conversations.createisland;

import com.itedya.skymaster.runnables.CreateIslandRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProvideIslandNamePrompt extends StringPrompt {
    private final int schematicId;

    public ProvideIslandNamePrompt(int schematicId) {
        this.schematicId = schematicId;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatColor.GRAY + "Podaj nazwę wyspy. Jeżeli chcesz wyjść, wpisz wyjdz.";
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) {
            context.getForWhom().sendRawMessage(ChatColor.YELLOW + "Nie podałeś nazwy! Jeżeli chcesz wyjść, wpisz wyjdz.");
            return new ProvideIslandNamePrompt(schematicId);
        }

        ThreadUtil.async(new CreateIslandRunnable((Player) context.getForWhom(), schematicId, input));

        return null;
    }
}
