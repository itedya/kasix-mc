package com.itedya.skymaster.conversations.createislandschematic.prompts;

import com.fastasyncworldedit.core.FaweAPI;
import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldguard.WorldGuard;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheckWorldEditClipboardPrompt extends ValidatingPrompt {
    @Override
    protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
        return input.equalsIgnoreCase("ok");
    }

    @Override
    protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
        Player player = (Player) context.getForWhom();

        Clipboard clipboard;

        try {
            Actor actor = BukkitAdapter.adapt(player);
            SessionManager manager = WorldEdit.getInstance().getSessionManager();
            LocalSession localSession = manager.get(actor);
            ClipboardHolder clipboardHolder = localSession.getClipboard();
            clipboard = clipboardHolder.getClipboards().stream().findFirst().orElse(null);
            if (clipboard == null) {
                throw new EmptyClipboardException();
            }
        } catch (EmptyClipboardException ex) {
            player.sendRawMessage(ChatUtil.PREFIX + " " + ChatUtil.p("&cMusisz na początku skopiować bloki przez world edita!"));
            return new CheckWorldEditClipboardPrompt();
        }

        context.setSessionData("clipboard", clipboard);

        return new CheckSpawnPositionPrompt();
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatUtil.PREFIX + " " +
                ChatUtil.p("&7Skopiuj bloki przez worldedita i wpisz &aok&7. ") +
                context.getSessionData("exitMessage");
    }
}
