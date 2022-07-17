package com.itedya.skymaster.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public abstract class SubCommand implements CommandExecutor, TabCompleter {
    public final String permission;

    public SubCommand(String permission) {
        this.permission = permission;
    }
}
