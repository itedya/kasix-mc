package com.itedya.kasixmc.prompts.createislandschematic;

import com.itedya.kasixmc.KasixMC;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

public class ProvideIslandSchematicFileNamePrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatColor.GRAY + "Podaj nazwę pliku schematu";
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) {
            context.getForWhom()
                    .sendRawMessage(ChatColor.RED + "Nie podałeś nazwy pliku schematu.");
            return new ProvideIslandSchematicFileNamePrompt();
        }

        KasixMC kasixMC = KasixMC.getInstance();

        Path path = Path.of(kasixMC.getDataFolder().getAbsolutePath(), "schematics", input);

        File file = new File(path.toString());

        if (! file.exists()) {
            context.getForWhom()
                    .sendRawMessage(ChatColor.RED + "Plik schematu o takiej nazwie nie istnieje!");
            return new ProvideIslandSchematicFileNamePrompt();
        }

        context.setSessionData("fileName", input);

        return new ProvideIslandSchematicMaterialPrompt();
    }
}
