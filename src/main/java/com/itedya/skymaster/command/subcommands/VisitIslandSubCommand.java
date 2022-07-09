package com.itedya.skymaster.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VisitIslandSubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Musisz być w grze, aby wykonać tą komendę!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Musisz podać nick gracza, którego chcesz odwiedzić");
            return true;
        }

        if (!player.hasPermission("skymaster.islands.visit")) {
            player.sendMessage(ChatColor.RED + "Brak permisji!");
            return true;
        }

        return true;
    }
}
