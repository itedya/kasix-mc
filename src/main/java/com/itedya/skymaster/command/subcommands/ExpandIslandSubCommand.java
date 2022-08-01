package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.dtos.ExpandIslandRequestDto;
import com.itedya.skymaster.runnables.expand.ExpandIslandGuiRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.IslandUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
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

    private List<ExpandIslandRequestDto> requests = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
        var location = player.getLocation();

        // Check if player is in world with islands
        if (!location.getWorld().getName().equals("world_islands")) {
            player.sendMessage(ChatColor.YELLOW + "Musisz być na wyspie, którą chcesz powiększyć, aby wykonać tą komendę!");
            return true;
        }

        // Get island that player is standing on
        var islandRegion = WorldGuardUtil.getRegionForLocation(location);

        if (islandRegion == null) {
            player.sendMessage(ChatColor.YELLOW + "Musisz być na wyspie, którą chcesz powiększyć, aby wykonać tą komendę!");
            return true;
        }

        var radius = (islandRegion.getMaximumPoint().getX() - islandRegion.getMinimumPoint().getX()) / 2;

        if (radius >= 500) {
            player.sendMessage(ChatColor.YELLOW + "Nie możesz mieć wyspy większej niż 1000 kratek!");
            return true;
        }

        // Calculate new radius
        int newRadius = radius + 50;

        // Get player uuid
        String playerUUID = player.getUniqueId().toString();

        ExpandIslandRequestDto request = requests.stream().filter(ele -> ele.uuid.equals(playerUUID)).findFirst().orElse(null);

        if (request == null) {
            int taskId = ThreadUtil.syncDelay(() -> {
                player.sendMessage(ChatColor.GREEN + "Minął czas akceptacji żądania powiększenia wyspy, anulowano akcje.");
                requests.removeIf(ele -> ele.uuid.equals(playerUUID));
            }, 20 * 30);

            request = new ExpandIslandRequestDto();
            request.taskId = taskId;
            request.uuid = playerUUID;

            requests.add(request);

            var message = new ComponentBuilder()
                    .color(ChatColor.GREEN)
                    .append("Powiększenie wyspy z ")
                    .append(radius * 2 + "").bold(true)
                    .append(" do ").bold(false)
                    .append(newRadius * 2 + " kratek").bold(true)
                    .append(" będzie kosztować ").bold(false)
                    .append(IslandUtil.getExpandCost(newRadius) + "$").color(ChatColor.YELLOW).bold(true)
                    .append(". ").color(ChatColor.GREEN).bold(false)
                    .append("Jeżeli akceptujesz, to wpisz komendę jeszcze raz.")
                    .create();

            player.sendMessage(message);

            return true;
        }

        // else
        requests.removeIf(ele -> ele.uuid.equals(playerUUID));

        var islandId = Integer.parseInt(islandRegion.getId().replaceAll("island_", ""));

        ThreadUtil.async(new ExpandIslandGuiRunnable(player, islandId, radius + 50, false));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
