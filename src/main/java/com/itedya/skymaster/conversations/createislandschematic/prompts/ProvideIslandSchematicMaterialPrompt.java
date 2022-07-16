package com.itedya.skymaster.conversations.createislandschematic.prompts;

import com.itedya.skymaster.dtos.database.IslandSchematicDto;
import com.itedya.skymaster.runnables.schematics.SaveIslandSchematicRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
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
        return ChatColor.GRAY + "Podaj materiał reprezentacyjny schematu";
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

        IslandSchematicDto dto = new IslandSchematicDto();
        dto.setName((String) context.getSessionData("name"));
        dto.setDescription((String) context.getSessionData("description"));
        dto.setFilePath((String) context.getSessionData("fileName"));
        dto.setMaterial(material);

        ThreadUtil.async(new SaveIslandSchematicRunnable(conversable, dto));

        conversable.sendRawMessage(ChatColor.GREEN + "Przyjęto do realizacji, serwer za chwilę spróbuje zapisać schemat. Daj mu chwilkę ;)");

        return null;
    }
}
