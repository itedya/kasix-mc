package com.itedya.skymaster.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtil {
    public static String getServerErrorMessage() {
        return ChatColor.RED + "Wystąpił błąd";
    }

    public static TextComponent baseComponentToTextComponent(BaseComponent[] components) {
        var textComponent = new TextComponent();

        for (var comp : components) {
            textComponent.addExtra(comp);
        }

        return textComponent;
    }
}
