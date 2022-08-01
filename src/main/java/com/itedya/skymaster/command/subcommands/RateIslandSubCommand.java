package com.itedya.skymaster.command.subcommands;

import com.itedya.skymaster.command.SubCommand;
import com.itedya.skymaster.runnables.block.UnblockPlayerFromVisitIslandRunnable;
import com.itedya.skymaster.runnables.island.RatePlayerIslandRunnable;
import com.itedya.skymaster.utils.ChatUtil;
import com.itedya.skymaster.utils.ThreadUtil;
import com.itedya.skymaster.utils.WorldGuardUtil;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RateIslandSubCommand extends SubCommand {

    public RateIslandSubCommand() {
        super("skymaster.islands.rate");

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        try {
            /*
            * /wyspa ocen [punkty/char/string]
            * */
            // check if user is in game
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatUtil.YOU_HAVE_TO_BE_IN_GAME);
                return true;
            }
            // check if user has permission   skymaster.islands.rate
            if (!player.hasPermission(this.permission)) {
                commandSender.sendMessage(ChatUtil.NO_PERMISSION);
                return true;
            }

            if(args.length != 1){
                commandSender.sendMessage("Niepoprawna ilosc argumentow");
                return true;
            }

            int arg1 = Integer.parseInt(args[1]);
            if( arg1 < -1 || arg1 > 1){
                commandSender.sendMessage("Niepoprawna wartosc podanego argumentu, podaj -1 lub 1 lub 0");
                return true;
            }

            //.. based on location ->
            Location locationToRate = player.getLocation();

            //check foe user's current world
            if(!locationToRate.getWorld().getName().equals("world_islands") ){
                commandSender.sendMessage("Musisz przejść na świat z wyspami");
                return true;
            }

            //check the location and location's island owner
            ProtectedRegion visitedRegion = WorldGuardUtil.getRegionForLocation(locationToRate);
            if(visitedRegion == null){
                commandSender.sendMessage("Musisz być na wyspie");
                return true;
            }
            //get island's id based on region
            String islandId = visitedRegion.getId().replaceAll("island_","");
            int casted_islandId = Integer.getInteger(islandId);

           ThreadUtil.async(new RatePlayerIslandRunnable(player,casted_islandId,arg1));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            commandSender.sendMessage(ChatColor.RED + "Wystąpił błąd serwera");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
