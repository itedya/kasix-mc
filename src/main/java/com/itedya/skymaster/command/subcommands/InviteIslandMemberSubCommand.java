package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.runnables.invite.ShowIslandsForInvitesGuiRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InviteIslandMemberSubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Musisz być w grze, aby wykonać tą komendę!");
                return true;
            }

            // check arguments
            if (args.length == 0) {
                player.sendMessage(ChatColor.YELLOW + "Nie wprowadziłeś nicku gracza, którego chcesz zaprosić.");
                return true;
            } else if (args.length >= 2) {
                player.sendMessage(ChatColor.YELLOW + "Podałeś za dużo argumentów");
            }

            // check permissions
            if (!player.hasPermission("skymaster.islands.invite")) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Brak permisji!");
                return true;
            }

            // check if player wants to invite himself
            if (args[0].equals(player.getName())) {
                player.sendMessage(ChatColor.YELLOW + "Nie możesz zaprosić sam siebie!");
                return true;
            }

            // get player to invite
            Player invitedPlayer = Bukkit.getPlayer(args[0]);

            // check if player to invite is online
            if (invitedPlayer == null) {
                player.sendMessage(ChatColor.YELLOW + "Gracz z takim nickiem nie istnieje albo nie jest online.");
                return true;
            }

            // check if player already has invitation
            IslandInviteDao islandInviteDao = IslandInviteDao.getInstance();
            if (islandInviteDao.doesPlayerHaveInvite(invitedPlayer.getUniqueId().toString())) {
                player.sendMessage(ChatColor.YELLOW + "Gracz " + invitedPlayer.getName() + " ma już zaproszenie na wyspę, poczekaj do 60 sekund aż wygaśnie.");
                return true;
            }

            // request
            ThreadUtil.async(new ShowIslandsForInvitesGuiRunnable(player, player, invitedPlayer, true));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Server error.");
        }
        return true;
    }
}