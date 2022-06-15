package com.itedya.skymaster.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtil {
    public static BaseComponent[] getServerErrorMessage() {
        return new ComponentBuilder()
                .color(ChatColor.RED)
                .append("Wystąpił błąd").bold(true)
                .create();
    }

    public static TextComponent baseComponentToTextComponent(BaseComponent[] components) {
        var textComponent = new TextComponent();

        for (var comp : components) {
            textComponent.addExtra(comp);
        }

        return textComponent;
    }
}
