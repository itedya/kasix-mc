package com.itedya.skymaster.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

public class ChatUtil {
    public static final String SERVER_ERROR = ChatColor.RED + "Wystąpił błąd";
    public static final String NO_PERMISSION = ChatColor.RED + "Brak permisji!";
    public static final String YOU_HAVE_TO_BE_IN_GAME = ChatColor.RED + "Musisz być w grze, aby użyć tej komendy!";
    public static final String PREFIX = ChatUtil.p("&1[&9Wyspy&1]&7");

    public static String p(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void sendWarning(Player player, String msg) {
        sendWarning(player, new ComponentBuilder()
                .append(msg)
                .create());
    }

    public static void sendWarning(Player player, BaseComponent[] components) {
        player.sendMessage(new ComponentBuilder()
                .append(ChatUtil.PREFIX)
                .append(" ").color(ChatColor.YELLOW)
                .append(components)
                .create());
    }
}
