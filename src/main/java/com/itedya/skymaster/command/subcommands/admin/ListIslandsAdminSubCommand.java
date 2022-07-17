package com.itedya.skymaster.command.subcommands.admin;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.runnables.list.ShowIslandListGuiRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ListIslandsAdminSubCommand extends SubCommand {
    public ListIslandsAdminSubCommand() {
        super("skymaster.admin.islands.list");
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
            player.sendMessage(ChatColor.YELLOW + "Podaj nick gracza");
            return true;
        }

        OfflinePlayer playerToCheck = Bukkit.getOfflinePlayer(args[0]);

        ThreadUtil.async(new ShowIslandListGuiRunnable(player, playerToCheck));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("Nick gracza");
        }

        return new ArrayList<>();
    }
}
