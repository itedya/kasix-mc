package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.utils.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HelpSubCommand extends SubCommand {
    private final List<String> lines = new ArrayList<>();

    public HelpSubCommand() {
        super("skymaster.islands.help");
        lines.add(ChatUtil.p("&6/wyspa &3blokuj &a<nick> &7- &eZablokuj użytkownika o danym nicku, tak by nie mógł odwiedzać twoich wysp"));
        lines.add(ChatUtil.p("&6/wyspa &3stworz &7- &eStwórz wyspę"));
        lines.add(ChatUtil.p("&6/wyspa &3powieksz &7- &ePowieksz wyspe na której stoisz"));
        lines.add(ChatUtil.p("&6/wyspa &3zapros &a<nick> &7- &eZapros gracza do wyspy którą posiadasz"));
        lines.add(ChatUtil.p("&6/wyspa &3wyrzuc &7- &eWyrzuc czlonka z wyspy"));
        lines.add(ChatUtil.p("&6/wyspa &3wyjdz &7- &eWyjdź z wyspy (jako członek)"));
        lines.add(ChatUtil.p("&6/wyspa &3lista &7- &eWylistuj wyspy"));
        lines.add(ChatUtil.p("&6/wyspa &3ocen &7- &eOcen wyspe na ktorej jestes"));
        lines.add(ChatUtil.p("&6/wyspa &3ustawdom &7- &eUstaw dom wyspy"));
        lines.add(ChatUtil.p("&6/wyspa &3odblokuj &a<nick> &7- &eOdblokuj użytkownika o danym nicku, tam by mógł z powrotem odwiedzać twoje wyspy"));
        lines.add(ChatUtil.p("&6/wyspa &3odwiedz &a<nick> &7- &eOdwiedz wyspę użytkownika o danym nicku"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatUtil.SERVER_ERROR);
                return true;
            }

            if (!player.hasPermission(permission)) {
                player.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }

            player.sendMessage(ChatUtil.p(ChatUtil.PREFIX + " | &2POMOC"));
            for (var line : lines) {
                player.sendMessage(line);
            }
            player.sendMessage(ChatUtil.p(ChatUtil.PREFIX + " | &2POMOC"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return new ArrayList<>();
    }
}
