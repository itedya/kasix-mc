package com.itedya.skymaster.conversations.createislandschematic.prompts;

import com.itedya.skymaster.dtos.database.IslandSchematicDto;
import com.itedya.skymaster.runnables.schematics.SaveIslandSchematicRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.Clip;

public class CheckSpawnPositionPrompt extends ValidatingPrompt {
    @Override
    protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
        return input.equalsIgnoreCase("ok");
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
        Player player = (Player) context.getForWhom();

        Clipboard clipboard = (Clipboard) context.getSessionData("clipboard");
        assert clipboard != null;
        Region region = clipboard.getRegion();
        BlockVector3 minimumPoint = region.getMinimumPoint();

        Location playerLocation = player.getLocation();

        int xOffset = playerLocation.getBlockX() - minimumPoint.getBlockX();
        int yOffset = playerLocation.getBlockY() - minimumPoint.getBlockY();
        int zOffset = playerLocation.getBlockZ() - minimumPoint.getBlockZ();

        Material material = (Material) context.getSessionData("material");

        IslandSchematicDto dto = new IslandSchematicDto();
        dto.name = (String) context.getSessionData("name");
        dto.description = (String) context.getSessionData("description");
        dto.filePath = (String) context.getSessionData("fileName");
        dto.material = material;
        dto.spawnOffsetX = xOffset;
        dto.spawnOffsetY = yOffset;
        dto.spawnOffsetZ = zOffset;

        ThreadUtil.async(new SaveIslandSchematicRunnable(player, dto, clipboard));

        player.sendMessage(new ComponentBuilder()
                .append(ChatUtil.PREFIX + " ")
                .append("Przyjęto do realizacji. ").color(ChatColor.GREEN)
                .append("Serwer za chwilę spróbuje zapisać schemat.").color(ChatColor.GRAY)
                .create());

        return null;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatUtil.PREFIX + " " +
                ChatUtil.p("&7Ustaw się w pozycji spawnu i wpisz &aok&7. ") +
                context.getSessionData("exitMessage");
    }
}
