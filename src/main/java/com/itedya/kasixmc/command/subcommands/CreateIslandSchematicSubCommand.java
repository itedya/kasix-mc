package com.itedya.kasixmc.command.subcommands;

import com.itedya.kasixmc.KasixMC;
import com.itedya.kasixmc.prompts.createislandschematic.ProvideIslandSchematicIdPrompt;
import com.itedya.kasixmc.utils.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ExactMatchConversationCanceller;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateIslandSchematicSubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // check if user is in game
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ConfigUtil.getColouredString("messages.youHaveToBeInGame",
                    "Musisz być w grze, aby użyć tej komendy!"));
            return true;
        }

        Conversation conversation = new ConversationFactory(KasixMC.getInstance())
                .withConversationCanceller(new ExactMatchConversationCanceller("/wyjdz"))
                .withFirstPrompt(new ProvideIslandSchematicIdPrompt())
                .withLocalEcho(false)
                .buildConversation(player);

        conversation.begin();


        return true;
    }
}
