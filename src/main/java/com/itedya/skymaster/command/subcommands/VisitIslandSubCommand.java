package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.daos.VisitBlockDao;
import com.itedya.skymaster.runnables.view.ShowVisitIslandGUIRunnable;
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

public class VisitIslandSubCommand extends SubCommand {
    public VisitIslandSubCommand() {
        super("skymaster.islands.visit");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
            return true;
        }

        if (!player.hasPermission("skymaster.islands.visit")) {
            player.sendMessage(ChatUtil.NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Musisz podać nick gracza, którego chcesz odwiedzić");
            return true;
        }

        String ownerNickname = args[0];
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerNickname);

        if (owner.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "Nie ma po co odwiedzać samego siebie :P");
            return true;
        }


        ThreadUtil.async(new ShowVisitIslandGUIRunnable(player, owner));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("Nick gracza którego wyspę chcesz odwiedzić");
        }

        return new ArrayList<>();
    }
}
