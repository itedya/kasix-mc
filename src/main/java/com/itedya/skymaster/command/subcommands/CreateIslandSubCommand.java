package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.runnables.island.ShowCreateIslandGuiRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class CreateIslandSubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            // check if user is in game
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Musisz być w grze, aby użyć tej komendy!");
                return true;
            }

            // check if user has permission
            if (!player.hasPermission("skymaster.islands.create")) {
                sender.sendMessage(ChatColor.RED + "Brak permisji!");
                return true;
            }

            ThreadUtil.async(new ShowCreateIslandGuiRunnable(player));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
            return true;
        }
    }
}
