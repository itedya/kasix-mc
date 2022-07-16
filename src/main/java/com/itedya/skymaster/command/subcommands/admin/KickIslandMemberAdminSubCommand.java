package com.itedya.skymaster.command.subcommands.admin;

import com.itedya.skymaster.runnables.kick.ShowIslandsForKickRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KickIslandMemberAdminSubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Musisz być w grze, aby użyć tej komendy!");
            return true;
        }

        if (!player.hasPermission("skymaster.admin.islands.kick")) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Brak permisji!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.YELLOW + "Musisz podać jeden argument - " + ChatColor.GOLD + "właściciel wyspy");
            return true;
        }

        OfflinePlayer playerToKick = Bukkit.getOfflinePlayer(args[0]);

        ThreadUtil.async(new ShowIslandsForKickRunnable(player, playerToKick));

        return true;
    }
}
