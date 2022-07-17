package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.runnables.list.ShowIslandListGuiRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ListIslandsSubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // check if user is in game
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
            return true;
        }

        Player playerToCheck;

        // check if user is checking someone's islands
        if (args.length > 0) {
            // check if user has permission to list someone's islands
            if (!player.hasPermission("skymaster.islands.list-someone")) {
                player.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }

            // todo: this can throw unexpected errors, change it to offline player
            playerToCheck = Bukkit.getPlayer(args[0]);

            if (playerToCheck == null) {
                player.sendMessage(ChatColor.YELLOW + "Gracz z takim nickiem nie istnieje!");
                return true;
            }
        } else {
            // check if user has permission to list their islands
            if (!player.hasPermission("skymaster.islands.list")) {
                player.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }

            playerToCheck = player;
        }

        ThreadUtil.async(new ShowIslandListGuiRunnable(player, playerToCheck));

        return true;
    }
}
