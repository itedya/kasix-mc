package com.itedya.skymaster.utils;

import com.itedya.skymaster.SkyMaster;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;

public class CommandUtil {
    public static void registerCommand(Command command) {
        SkyMaster plugin = SkyMaster.getInstance();
        
        try {
            CommandMap commandMap = null;

            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) (field.get(plugin.getServer().getPluginManager()));

            assert commandMap != null;
            commandMap.register("skymaster", command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
