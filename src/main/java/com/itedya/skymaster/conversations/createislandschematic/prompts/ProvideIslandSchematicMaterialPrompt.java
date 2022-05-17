package com.itedya.skymaster.prompts.createislandschematic;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.IslandSchematicDao;
import com.itedya.skymaster.dtos.IslandSchematicDto;
import com.itedya.skymaster.exceptions.ServerError;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

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

        Material material = null;
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

        ThreadUtil.async(new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    IslandSchematicDao islandSchematicDao = IslandSchematicDao.getInstance();
                    islandSchematicDao.create(dto);
                    conversable.sendRawMessage(ChatColor.GREEN + "Pomyślnie zapisano schemat.");
                } catch (ServerError e) {
                    SkyMaster.getInstance().getLogger().log(Level.SEVERE, e.getMessage(), e);
                    conversable.sendRawMessage(ChatColor.RED + "Wystąpił błąd serwera.");
                }
            }
        });

        conversable.sendRawMessage(ChatColor.GREEN + "Przyjęto do realizacji, serwer za chwilę spróbuje zapisać schemat. Daj mu chwilkę ;)");

        return null;
    }


}