package com.itedya.skymaster.conversations.createislandschematic.prompts;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.conversations.ConversationPrompt;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class ProvideIslandSchematicFileNamePrompt implements ConversationPrompt {
    @Override
    public String getMessage(Player player) {
        return ChatColor.GRAY + "Podaj nazwę pliku schematu";
    }

    @Override
    public ConversationPrompt parse(Map session, Player player, String message) {
        if (message == null) {
            player.sendMessage(ChatColor.RED + "Nie podałeś nazwy pliku schematu.");
            return new ProvideIslandSchematicFileNamePrompt();
        }

        SkyMaster plugin = SkyMaster.getInstance();
        Path path = Path.of(plugin.getDataFolder().getAbsolutePath(), "schematics", message);
        File file = new File(path.toString());

        if (!file.exists()) {
            player.sendMessage(ChatColor.RED + "Plik schematu o takiej nazwie nie istnieje!");
            return new ProvideIslandSchematicFileNamePrompt();
        }

        session.put("fileName", message);

        return new ProvideIslandSchematicMaterialPrompt();
    }
}
