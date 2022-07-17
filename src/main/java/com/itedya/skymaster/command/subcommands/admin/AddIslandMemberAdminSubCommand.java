package com.itedya.skymaster.command.subcommands.admin;

import com.itedya.skymaster.runnables.invite.ShowIslandsForInvitesGuiRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddIslandMemberAdminSubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
            return true;
        }

        if (!player.hasPermission("skymaster.admin.islands.add-member")) {
            player.sendMessage(ChatUtil.NO_PERMISSION);
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(new ComponentBuilder()
                    .color(ChatColor.YELLOW)
                    .append("Musisz podać dwa argumenty: ")
                    .append("nazwa członka do zaprosienia").color(ChatColor.GOLD)
                    .append(" oraz ").color(ChatColor.YELLOW)
                    .append("właściciel wyspy").color(ChatColor.GOLD)
                    .create());

            return true;
        }

        OfflinePlayer toAdd = Bukkit.getOfflinePlayer(args[0]);
        OfflinePlayer islandOwner = Bukkit.getOfflinePlayer(args[1]);

        ThreadUtil.async(new ShowIslandsForInvitesGuiRunnable(player, islandOwner, toAdd, false));

        return true;
    }
}
