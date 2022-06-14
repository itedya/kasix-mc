package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.runnables.invite.ShowIslandsForInvitesGuiRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddIslandMemberSubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Musisz być w grze, aby wykonać tą komendę!");
                return true;
            }

            if (!player.hasPermission("skymaster.islands.invite")) {
                player.sendMessage(ChatColor.RED + "Brak permisji.");
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(ChatColor.YELLOW + "Nie wprowadziłeś nicku gracza, którego chcesz zaprosić.");
                return true;
            }

            if (args[0].equals(player.getName())) {
                player.sendMessage(ChatColor.YELLOW + "Nie możesz zaprosić sam siebie!");
                return true;
            }

            Player invitedPlayer = Bukkit.getPlayer(args[0]);
            if (invitedPlayer == null) {
                player.sendMessage(ChatColor.YELLOW + "Gracz z takim nickiem nie istnieje albo nie jest online.");
                return true;
            }

            IslandInviteDao islandInviteDao = IslandInviteDao.getInstance();
            if (islandInviteDao.doesPlayerHaveInvite(invitedPlayer.getUniqueId().toString())) {
                player.sendMessage(ChatColor.YELLOW + "Gracz " + invitedPlayer.getName() + " ma już zaproszenie na wyspę, poczekaj do 60 sekund aż wygaśnie.");
                return true;
            }

            ThreadUtil.async(new ShowIslandsForInvitesGuiRunnable(player, invitedPlayer));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Server error.");
        }
        return true;
    }
}
