package com.itedya.kasixmc.utils;

import com.itedya.kasixmc.KasixMC;
import com.itedya.kasixmc.command.IslandCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;

public class CommandUtil {
    public static void registerCommand(Command command) {
        KasixMC plugin = KasixMC.getInstance();
        
        try {
            CommandMap commandMap = null;

            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) (field.get(plugin.getServer().getPluginManager()));

            assert commandMap != null;
            commandMap.register("kasixmc", command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
