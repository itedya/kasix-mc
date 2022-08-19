package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.runnables.invite.AcceptInviteToIslandRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AcceptInviteToIslandSubCommand extends SubCommand {
    public AcceptInviteToIslandSubCommand() {
        super("skymaster.islands.accept-invite");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
            return true;
        }

        if (!player.hasPermission("skymaster.islands.accept-invite")) {
            sender.sendMessage(ChatUtil.NO_PERMISSION);
            return true;
        }

        var dao = IslandInviteDao.getInstance();

        if (args.length == 0) {
            player.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Musisz podać nick gracza od którego dostałeś zaproszenie!").color(ChatColor.YELLOW)
                    .create());
            return true;
        }

        OfflinePlayer invitingPlayer = Bukkit.getOfflinePlayer(args[0]);

        var invite = dao.get(player.getUniqueId().toString(), invitingPlayer.getUniqueId().toString());

        if (invite == null) {
            player.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Zaproszenie wygasło!").color(ChatColor.YELLOW)
                    .create());
            return true;
        }

        dao.remove(player.getUniqueId().toString(), invitingPlayer.getUniqueId().toString());

        ThreadUtil.async(new AcceptInviteToIslandRunnable(player, invite));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
