package com.itedya.skymaster.command;

import com.itedya.skymaster.command.subcommands.admin.AddIslandMemberAdminSubCommand;
import com.itedya.skymaster.command.subcommands.admin.RemoveIslandMemberAdminSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AdminCommand implements CommandExecutor {
    public final Map<String, CommandExecutor> executorMap = new HashMap<>(Map.of(
            "zapros", new AddIslandMemberAdminSubCommand(),
            "wyrzuc", new RemoveIslandMemberAdminSubCommand()
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
}
