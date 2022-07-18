package com.itedya.skymaster.command;

import com.itedya.skymaster.command.subcommands.*;
import com.itedya.skymaster.command.subcommands.admin.CreateIslandSchematicAdminSubCommand;
import com.itedya.skymaster.runnables.block.UnblockPlayerFromVisitIslandRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IslandCommand extends Command {
    public IslandCommand(@NotNull String name) { super(name);}
    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return executorMap.keySet()
                    .stream()
                    .toList();
        }
        return new ArrayList<>();
    }

    public final Map<String, CommandExecutor> executorMap = new HashMap<>(Map.ofEntries(
            Map.entry("stworz", new CreateIslandSubCommand()),
            Map.entry("lista", new ListIslandsSubCommand()),
            Map.entry("ustawdom", new SetIslandHomeSubCommand()),
            Map.entry("zapros", new InviteIslandMemberSubCommand()),
            Map.entry("akceptuj", new AcceptInviteToIslandSubCommand()),
            Map.entry("wyrzuc", new KickIslandMemberSubCommand()),
            Map.entry("powieksz", new ExpandIslandSubCommand()),
            Map.entry("odwiedz", new VisitIslandSubCommand()),
            Map.entry("blokuj", new BlockVisitIslandSubCommand()),
            Map.entry("odblokuj", new UnblockVisitIslandSubCommand())
    ));

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Podaj nazwę komendy");
            return true;
        }
        CommandExecutor commandExecutor = executorMap.get(args[0]);
        if (commandExecutor == null) {
            sender.sendMessage(ChatColor.RED + "Taka komenda nie istnieje!");
            return true;
        }
        commandExecutor.onCommand(sender, this, commandLabel, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }
}
