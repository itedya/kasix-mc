package com.itedya.skymaster.command.subcommands.admin;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.daos.IslandCreationCooldownDao;
import com.itedya.skymaster.dtos.IslandCreationCooldownDto;
import com.itedya.skymaster.utils.ChatUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RemoveIslandCreationCooldownAdminSubCommand extends SubCommand {
    public RemoveIslandCreationCooldownAdminSubCommand() {
        super("skymaster.admin.islands.remove-island-creation-cooldown");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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
                    .append("Musisz podać nick użytkownika, któremu chcesz usunąć cooldown!").color(ChatColor.YELLOW)
                    .create());

            return true;
        }

        OfflinePlayer playerWithCooldown = Bukkit.getOfflinePlayer(args[0]);

        String playerWithCooldownUuid = playerWithCooldown.getUniqueId().toString();
        IslandCreationCooldownDao dao = IslandCreationCooldownDao.getInstance();

        IslandCreationCooldownDto dto = dao.getByPlayerUuid(playerWithCooldownUuid);

        if (dto == null) {
            player.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Ten użytkownik nie ma nałożonego cooldownu lub przed chwilą mu się skończył!").color(ChatColor.RED)
                    .create());
            return true;
        }

        if (args.length >= 2 && args[1].equals("--accept")) {
            dao.remove(playerWithCooldownUuid);

            player.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Cooldown użytkownika ").color(ChatColor.GREEN)
                    .append(args[0]).bold(true)
                    .append(" trwający ")
                    .append(dto.getStringHours()).bold(true)
                    .append(":").bold(false)
                    .append(dto.getStringMinutes()).bold(true)
                    .append(":").bold(false)
                    .append(dto.getStringSeconds()).bold(true)
                    .append(" został zdjęty.").create());

            return true;
        }

        // else
        TextComponent confirmButton = new TextComponent("[POTWIERDŹ]");
        confirmButton.setColor(ChatColor.GREEN);
        confirmButton.setBold(true);
        confirmButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wyspa admin usuncooldown " + args[0] + " --accept"));

        player.sendMessage(new ComponentBuilder()
                .append(ChatUtil.PREFIX + " ")
                .append("Czy na pewno chcesz zdjąć cooldown użytkownika ").color(ChatColor.YELLOW)
                .append(args[0]).bold(true)
                .append(" trwający jeszcze ").bold(false)
                .append(dto.getStringHours()).bold(true)
                .append(":").bold(false)
                .append(dto.getStringMinutes()).bold(true)
                .append(":").bold(false)
                .append(dto.getStringSeconds()).bold(true)
                .append("? ").bold(false)
                .append(confirmButton)
                .create());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender.hasPermission(permission)) {
            if (args.length == 1) {
                return List.of("Nick użytkownika, który ma cooldown");
            }
        }

        return new ArrayList<>();
    }
}
