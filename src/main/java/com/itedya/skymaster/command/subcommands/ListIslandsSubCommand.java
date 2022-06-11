package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.runnables.ShowIslandListGuiRunnable;
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
            sender.sendMessage("Musisz być w grze, aby wykonać tą komendę.");
            return true;
        }

        Player playerToCheck;

        // check if user is checking someone's islands
        if (args.length > 0) {
            // check if user has permission to list someone's islands
            if (!player.hasPermission("kasix-mc.islands.list-someone")) {
                player.sendMessage(ChatColor.RED + "Brak permisji.");
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
            if (!player.hasPermission("kasix-mc.islands.list")) {
                player.sendMessage(ChatColor.RED + "Brak permisji.");
                return true;
            }

            playerToCheck = player;
        }

        ThreadUtil.async(new ShowIslandListGuiRunnable(player, playerToCheck));

        return true;
    }
}
