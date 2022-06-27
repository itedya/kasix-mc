package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.runnables.expand.ExpandIslandGuiRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpandIslandSubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Musisz być w grze, aby wykonać tą komendę!");
            return true;
        }

        if (!player.hasPermission("skymaster.islands.expand")) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Brak permisji!");
            return true;
        }

        var location = player.getLocation();

        if (!location.getWorld().getName().equals("world_islands")) {
            player.sendMessage(ChatColor.YELLOW + "Musisz być na wyspie, którą chcesz powiększyć, aby wykonać tą komendę!");
            return true;
        }

        var vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        var regionManager = WorldGuardUtil.getRegionManager();
        var applicableRegions = regionManager.getApplicableRegions(vector);

        ProtectedRegion islandRegion = null;

        for (var region : applicableRegions) {
            if (region.getId().startsWith("island_")) {
                islandRegion = region;
                break;
            }
        }

        if (islandRegion == null) {
            player.sendMessage(ChatColor.YELLOW + "Musisz być na wyspie, którą chcesz powiększyć, aby wykonać tą komendę!");
            return true;
        }

        var radius = (islandRegion.getMaximumPoint().getX() - islandRegion.getMinimumPoint().getX()) / 2;

        if (radius >= 500) {
            player.sendMessage(ChatColor.YELLOW + "Nie możesz mieć wyspy większej niż 1000 kratek!");
            return true;
        }

        var islandId = Integer.parseInt(islandRegion.getId().replaceAll("island_", ""));

        ThreadUtil.async(new ExpandIslandGuiRunnable(player, islandId, radius + 50, false));

        return true;
    }
}
