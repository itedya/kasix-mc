package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.command.subcommands.admin.AddIslandMemberAdminSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AdminSubCommand extends SubCommand {
    public final Map<String, SubCommand> executorMap = new HashMap<>(Map.of(
//            "stworzschemat", new CreateIslandSchematicAdminSubCommand(),
            "zapros", new AddIslandMemberAdminSubCommand()
//            "wyrzuc", new KickIslandMemberAdminSubCommand(),
//            "powieksz", new ExpandIslandAdminSubCommand()
    ));

    public AdminSubCommand() {
        super("skymaster.admin.islands");
    }

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
        if (args.length == 1) {
            return executorMap.keySet()
                    .stream().filter(key -> {
                        var ex = executorMap.get(key);
                        if (ex == null) return true;
                        return sender.hasPermission(ex.permission);
                    }).toList();
        }

        var commandExecutor = executorMap.get(args[0]);
        if (commandExecutor != null) {
            return commandExecutor.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
        }

        return new ArrayList<>();
    }
}
