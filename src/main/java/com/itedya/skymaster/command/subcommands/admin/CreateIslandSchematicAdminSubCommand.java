package com.itedya.skymaster.command.subcommands.admin;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.conversations.createislandschematic.prompts.ProvideIslandSchematicNamePrompt;
import com.itedya.skymaster.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ExactMatchConversationCanceller;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreateIslandSchematicAdminSubCommand extends SubCommand {
    public CreateIslandSchematicAdminSubCommand() {
        super("skymaster.admin.islands.createschematic");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // check if user is in game
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
            return true;
        }

        if (!player.hasPermission(permission)) {
            player.sendMessage(ChatUtil.NO_PERMISSION);
            return true;
        }

        Conversation conversation = new ConversationFactory(SkyMaster.getInstance())
                .withConversationCanceller(new ExactMatchConversationCanceller("wyjdz"))
                .withFirstPrompt(new ProvideIslandSchematicNamePrompt())
                .withLocalEcho(false)
                .buildConversation(player);

        conversation.begin();

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
