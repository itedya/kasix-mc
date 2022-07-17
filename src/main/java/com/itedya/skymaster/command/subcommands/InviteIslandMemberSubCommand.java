package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.runnables.invite.ShowIslandsForInvitesGuiRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InviteIslandMemberSubCommand extends SubCommand {
    public InviteIslandMemberSubCommand() {
        super("skymaster.islands.invite");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(ChatColor.YELLOW + "Nie wprowadziłeś nicku gracza, którego chcesz zaprosić.");
                return true;
            }

            // check permissions
            if (!player.hasPermission(permission)) {
                player.sendMessage(ChatUtil.NO_PERMISSION);
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("Nick gracza którego chcesz zaprosić");
        }

        return new ArrayList<>();
    }
}
