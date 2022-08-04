package com.itedya.skymaster.command;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.command.subcommands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IslandCommand extends SubCommand {
    public static void register() {
        var plugin = SkyMaster.getInstance();
        var instance = new IslandCommand();

        var command = plugin.getCommand("wyspa");
        assert command != null : "Command is null!";

        command.setExecutor(instance);
        command.setTabCompleter(instance);
    }

    public IslandCommand() {
        super(null);
    }

    public final Map<String, SubCommand> executorMap = new HashMap<>(Map.ofEntries(
            Map.entry("stworz", new CreateIslandSubCommand()),
            Map.entry("lista", new ListIslandsSubCommand()),
            Map.entry("ustawdom", new SetIslandHomeSubCommand()),
            Map.entry("zapros", new InviteIslandMemberSubCommand()),
            Map.entry("akceptuj", new AcceptInviteToIslandSubCommand()),
            Map.entry("wyrzuc", new KickIslandMemberSubCommand()),
            Map.entry("powieksz", new ExpandIslandSubCommand()),
            Map.entry("odwiedz", new VisitIslandSubCommand()),
            Map.entry("blokuj", new BlockVisitIslandSubCommand()),
            Map.entry("odblokuj", new UnblockVisitIslandSubCommand()),
            Map.entry("admin", new AdminSubCommand()),
            Map.entry("pomoc", new HelpSubCommand())
    ));

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Podaj nazwę komendy");
            return true;
        }

        CommandExecutor commandExecutor = executorMap.get(args[0]);
        if (commandExecutor == null) {
            sender.sendMessage(ChatColor.RED + "Taka komenda nie istnieje!");
            return true;
        }

        commandExecutor.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) {
            return new ArrayList<>();
        } else if (args.length == 1) {
            return executorMap.keySet().stream()
                    .filter(key -> {
                        var ex = executorMap.get(key);
                        if (ex.permission == null) return true;
                        return sender.hasPermission(ex.permission);
                    }).toList();
        }

        var ex = executorMap.get(args[0]);
        if (ex == null) return new ArrayList<>();

        return ex.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
    }
}