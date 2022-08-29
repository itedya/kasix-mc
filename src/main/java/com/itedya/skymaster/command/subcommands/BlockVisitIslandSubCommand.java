package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.runnables.block.BlockPlayerFromVisitIslandRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockVisitIslandSubCommand extends SubCommand {

    public BlockVisitIslandSubCommand() {
        super("skymaster.islands.blockvisit");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        try {
            // check if user is in game
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
                return true;
            }
            // check if user has permission   skymaster.islands.blockvisit
            if (!player.hasPermission("skymaster.islands.blockvisit")) {
                sender.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(ChatColor.YELLOW + "Podaj nick gracza którego chcesz zablokować");
                return true;
            }

            if (args[0].equals(player.getName())) {
                player.sendMessage(ChatColor.YELLOW + "Nie możesz zablokować sam siebie!");
                return true;
            }

            OfflinePlayer playerToBlock = Bukkit.getOfflinePlayer(args[0]);

            ThreadUtil.async(new BlockPlayerFromVisitIslandRunnable(player, playerToBlock));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            var names = Bukkit.getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName);

            if (commandSender instanceof Player player) {
                return names.filter(ele -> !ele.equals(player.getName()))
                        .toList();
            }

            return names.toList();
        }

        return new ArrayList<>();
    }
}
