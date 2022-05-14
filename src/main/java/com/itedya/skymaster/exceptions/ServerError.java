package com.itedya.skymaster.exceptions;

import com.itedya.skymaster.SkyMaster;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public class ServerError extends Exception {
    public ServerError(String logMessage) {
        super(ChatColor.RED + "Błąd serwera, skontaktuj się z administratorem.");

        SkyMaster plugin = SkyMaster.getInstance();
        plugin.getLogger().log(Level.SEVERE, logMessage);
    }

    public ServerError() {
        super(ChatColor.RED + "Błąd serwera, skontaktuj się z administratorem.");
    }
}
