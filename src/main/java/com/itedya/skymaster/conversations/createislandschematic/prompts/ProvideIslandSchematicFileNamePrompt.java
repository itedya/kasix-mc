package com.itedya.skymaster.conversations.createislandschematic.prompts;

import com.itedya.skymaster.SkyMaster;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
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
        Conversable player = context.getForWhom();

        if (input == null) {
            player.sendRawMessage(ChatColor.RED + "Nie podałeś nazwy pliku schematu.");
            return new ProvideIslandSchematicFileNamePrompt();
        }

        SkyMaster plugin = SkyMaster.getInstance();
        Path path = Path.of(plugin.getDataFolder().getAbsolutePath(), "schematics", input);
        File file = new File(path.toString());

        if (!file.exists()) {
            player.sendRawMessage(ChatColor.RED + "Plik schematu o takiej nazwie nie istnieje!");
            return new ProvideIslandSchematicFileNamePrompt();
        }

        context.setSessionData("fileName", input);

        return new ProvideIslandSchematicMaterialPrompt();
    }
}
