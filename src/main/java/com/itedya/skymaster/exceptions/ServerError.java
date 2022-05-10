package com.itedya.skymaster.exceptions;

import org.bukkit.ChatColor;

public class ServerError extends Exception {
    public ServerError() {
        super(ChatColor.RED + "Błąd serwera, skontaktuj się z administratorem.");
    }
}
