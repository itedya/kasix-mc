package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.SkyMaster;
import com.itedya.skymaster.daos.Database;
import com.itedya.skymaster.daos.IslandDao;
import com.itedya.skymaster.daos.IslandHomeDao;
import com.itedya.skymaster.dtos.IslandDto;
import com.itedya.skymaster.dtos.IslandHomeDto;
import com.itedya.skymaster.exceptions.ServerError;
import com.itedya.skymaster.runnables.SetIslandHomeRunnable;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class SetIslandHomeSubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Musisz być w grze, aby wykonać tą komendę!");
                return true;
            }

            if (!player.getWorld().getName().equals("world_islands")) {
                player.sendMessage(ChatColor.YELLOW + "Jesteś w złym świecie! Przejdź na wyspy.");
                return true;
            }

            Location playerLocation = player.getLocation();

            ThreadUtil.async(new SetIslandHomeRunnable(player, playerLocation));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Server error.");
        }

        return true;
    }
}
