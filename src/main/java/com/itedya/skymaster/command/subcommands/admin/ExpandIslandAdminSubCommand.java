package com.itedya.skymaster.command.subcommands.admin;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.runnables.expand.ExpandIslandGuiRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ExpandIslandAdminSubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
                return true;
            }

            if (!player.hasPermission("skymaster.admin.islands.expand")) {
                player.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(ChatColor.YELLOW + "Musisz podać tylko wielkość wyspy");
                return true;
            }


            int radius = Integer.parseInt(args[0]) / 2;

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

            var islandId = Integer.parseInt(islandRegion.getId().replaceAll("island_", ""));

            ThreadUtil.async(new ExpandIslandGuiRunnable(player, islandId, radius, true));
        } catch (Exception e) {
            sender.sendMessage(ChatUtil.SERVER_ERROR);
            e.printStackTrace();
        }

        return true;
    }
}
