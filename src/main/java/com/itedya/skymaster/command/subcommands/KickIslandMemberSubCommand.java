package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.runnables.kick.ShowIslandsForKickRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KickIslandMemberSubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // check sender type
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Musisz być w grze, żeby wykonać tą komendę");
            return true;
        }

        OfflinePlayer toCheck = player;

        // check permissions
        if (args.length > 0 && !args[0].equals(player.getName())) {
            if (!player.hasPermission("skymaster.admin.islands.kick")) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Brak permisji!");
                return true;
            } else {
                toCheck = Bukkit.getOfflinePlayer(args[0]);
            }
        } else {
            if (!player.hasPermission("skymaster.islands.kick")) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Brak permisji!");
                return true;
            }
        }

        ThreadUtil.async(new ShowIslandsForKickRunnable(player, toCheck));

        return true;
    }
}
