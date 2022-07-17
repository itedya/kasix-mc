package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.runnables.home.SetIslandHomeRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class SetIslandHomeSubCommand extends SubCommand {
    public SetIslandHomeSubCommand() {
        super("skymaster.islands.sethome");
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
                return true;
            }

            if (!player.hasPermission(permission)) {
                player.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }

            if (!player.getWorld().getName().equals("world_islands")) {
                player.sendMessage(ChatColor.YELLOW + "Jesteś w złym świecie! Przejdź na wyspy.");
                return true;
            }

            Location playerLocation = player.getLocation();

            ThreadUtil.async(new SetIslandHomeRunnable(player, playerLocation));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Server error.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
