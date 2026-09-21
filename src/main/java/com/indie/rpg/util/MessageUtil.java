package com.indie.rpg.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class MessageUtil {
    private MessageUtil() { }

    public static String color(String text) {
        return text == null ? "" : ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void send(CommandSender to, String text) {
        if (to != null && text != null) to.sendMessage(color(text));
    }

    public static void sendPrefix(CommandSender to, String prefix, String text) {
        send(to, prefix + text);
    }

    public static String strip(String text) {
        return text == null ? "" : ChatColor.stripColor(text);
    }
}