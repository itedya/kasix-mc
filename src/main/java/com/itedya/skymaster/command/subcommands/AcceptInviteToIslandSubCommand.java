package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.runnables.invite.AcceptInviteToIslandRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AcceptInviteToIslandSubCommand implements CommandExecutor {
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
        var invite = dao.getByToPlayerUuid(player.getUniqueId().toString());

        if (invite == null) {
            player.sendMessage(ChatColor.YELLOW + "Nie masz zaproszenia do żadnej wyspy lub zaproszenie wygasło!");
            return true;
        }

        dao.remove(player.getUniqueId().toString());

        ThreadUtil.async(new AcceptInviteToIslandRunnable(player, invite));

        return true;
    }
}
