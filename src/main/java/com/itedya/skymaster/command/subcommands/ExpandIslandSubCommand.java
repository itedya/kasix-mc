package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.runnables.expand.ExpandIslandGuiRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.IslandUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExpandIslandSubCommand extends SubCommand {
    public ExpandIslandSubCommand() {
        super("skymaster.islands.expand");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            // Check sender
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
                return true;
            }

            // Check permissions
            if (!player.hasPermission(permission)) {
                player.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }

            // Get player location
            Location location = player.getLocation();

            // Check if player is in world with islands
            if (!location.getWorld().getName().equals("world_islands")) {
                ChatUtil.sendWarning(player, "Musisz być na wyspie, którą chcesz powiększyć, aby wykonać tą komendę!");
                return true;
            }

            // Get island that player is standing on
            var islandRegion = WorldGuardUtil.getRegionForLocation(location);

            if (islandRegion == null) {
                ChatUtil.sendWarning(player, "Musisz być na wyspie, którą chcesz powiększyć, aby wykonać tą komendę!");
                return true;
            }

            var radius = (islandRegion.getMaximumPoint().getX() - islandRegion.getMinimumPoint().getX()) / 2;

            if (radius >= 500) {
                ChatUtil.sendWarning(player, "Nie możesz mieć wyspy większej niż 1000 kratek!");
                return true;
            }

            // Calculate new radius
            int newRadius = radius + 50;

            if (args.length >= 1 && args[0].equals("--accept")) {
                var islandId = Integer.parseInt(islandRegion.getId().replaceAll("island_", ""));

                ThreadUtil.async(new ExpandIslandGuiRunnable(player, islandId, radius + 50, false));
                return true;
            }

            TextComponent acceptButton = new TextComponent("[AKCEPTUJ]");
            acceptButton.setBold(true);
            acceptButton.setColor(ChatColor.GREEN);
            acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wyspa powieksz --accept"));

            player.sendMessage(new ComponentBuilder()
                    .append(ChatUtil.PREFIX + " ")
                    .append("Powiększenie wyspy z ").color(ChatColor.YELLOW)
                    .append(radius * 2 + "").bold(true)
                    .append(" do ").bold(false)
                    .append(newRadius * 2 + " kratek").bold(true)
                    .append(" będzie kosztować ").bold(false)
                    .append(IslandUtil.getExpandCost(newRadius) + "$").color(ChatColor.GOLD).bold(true)
                    .append(". ").color(ChatColor.GREEN).bold(false)
                    .append(acceptButton)
                    .create());
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatUtil.PREFIX + " " + ChatUtil.SERVER_ERROR);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
