package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.daos.IslandInviteDao;
import com.itedya.skymaster.dtos.database.IslandInviteDto;
import com.itedya.skymaster.runnables.invite.ShowIslandsForInvitesGuiRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
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

            if (!player.hasPermission(permission)) {
                player.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Nie wprowadziłeś nicku gracza, którego chcesz zaprosić.").color(ChatColor.YELLOW)
                        .create());
                return true;
            }

            // check if player wants to invite himself
            if (args[0].equals(player.getName())) {
                player.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Nie możesz zaprosić sam siebie :)").color(ChatColor.YELLOW)
                        .create());
                return true;
            }

            // get player to invite
            Player invitedPlayer = Bukkit.getPlayer(args[0]);

            // check if player to invite is online
            if (invitedPlayer == null) {
                player.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Gracz z takim nickiem nie istnieje albo nie jest online.").color(ChatColor.YELLOW)
                        .create());
                return true;
            }

            IslandInviteDao dao = IslandInviteDao.getInstance();
            IslandInviteDto dto = dao.get(invitedPlayer.getUniqueId().toString(), player.getUniqueId().toString());
            if (dto != null) {
                player.sendMessage(new ComponentBuilder()
                        .append(ChatUtil.PREFIX + " ")
                        .append("Już zaprosiłeś tego gracza! Odczekaj %d sekund aż tamto zaproszenie wygaśnie.".formatted(dto.ttl)).color(ChatColor.YELLOW)
                        .create());
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
            var names = Bukkit.getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName);

            if (sender instanceof Player player) {
                return names.filter(ele -> !ele.equals(player.getName()))
                        .toList();
            }

            return names.toList();
        }

        return new ArrayList<>();
    }
}
