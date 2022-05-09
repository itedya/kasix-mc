package com.itedya.kasixmc.prompts.createislandschematic;

import com.itedya.kasixmc.daos.IslandSchematicDao;
import com.itedya.kasixmc.dtos.IslandSchematicDto;
import com.itedya.kasixmc.utils.IslandSchematicUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class ProvideIslandSchematicMaterialPrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatColor.GRAY + "Podaj materiał reprezentacyjny schematu";
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null) {
            context.getForWhom()
                    .sendRawMessage(ChatColor.RED + "Nie podałeś materiału reprezentacyjnego.");
            return new ProvideIslandSchematicMaterialPrompt();
        }

        Material material = null;
        try {
            material = Material.valueOf(input);
        } catch (IllegalArgumentException e) {
            context.getForWhom()
                    .sendRawMessage(ChatColor.RED + "Materiał " + input + " nie istnieje!");
            return new ProvideIslandSchematicMaterialPrompt();
        }

        IslandSchematicDto dto = new IslandSchematicDto();
        dto.setId((String) context.getSessionData("id"));
        dto.setName((String) context.getSessionData("name"));
        dto.setDescription((String) context.getSessionData("description"));
        dto.setFilePath((String) context.getSessionData("fileName"));
        dto.setMaterial(material);

        try {
            IslandSchematicDao islandSchematicDao = IslandSchematicDao.getInstance();
            islandSchematicDao.create(dto);
        } catch (IOException e) {
            e.printStackTrace();
            context.getForWhom()
                    .sendRawMessage(ChatColor.RED + "Błąd serwera, akcja anulowana.");
        }

        context.getForWhom()
                .sendRawMessage(ChatColor.GREEN + "Dodano schemat.");

        return null;
    }
}
