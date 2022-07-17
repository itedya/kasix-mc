package com.itedya.skymaster.command.subcommands.admin;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.runnables.kick.ShowIslandsForKickRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KickIslandMemberAdminSubCommand extends SubCommand {
    public KickIslandMemberAdminSubCommand() {
        super("skymaster.admin.islands.kick");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
            return true;
        }

        if (!player.hasPermission(permission)) {
            player.sendMessage(ChatUtil.NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Musisz podać " + ChatColor.GOLD + "właściciela wyspy");
            return true;
        }

        OfflinePlayer playerToKick = Bukkit.getOfflinePlayer(args[0]);

        ThreadUtil.async(new ShowIslandsForKickRunnable(player, playerToKick));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("Właściciel wyspy");
        }
        return new ArrayList<>();
    }
}
