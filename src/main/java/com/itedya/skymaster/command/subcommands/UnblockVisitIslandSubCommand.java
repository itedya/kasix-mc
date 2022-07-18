package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.runnables.block.BlockPlayerFromVisitIslandRunnable;
import com.itedya.skymaster.runnables.block.UnblockPlayerFromVisitIslandRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UnblockVisitIslandSubCommand extends SubCommand {

    public UnblockVisitIslandSubCommand(){ super("skymaster.islands.unblockvisit");}
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            // check if user is in game
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
                return true;
            }
            // check if user has permission   skymaster.islands.unblockvisit
            if (!player.hasPermission(this.permission)) {
                sender.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }
            ThreadUtil.async(new UnblockPlayerFromVisitIslandRunnable(player, player));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return new ArrayList<>();
    }
}
